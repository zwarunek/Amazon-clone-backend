package com.zacharywarunek.amazonclone.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationToken;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenRepo;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class RegistrationControllerTest {


    @Autowired
    AccountService accountService;
    @Autowired
    ConfirmationTokenService confirmationTokenService;
    @Autowired
    RegistrationService registrationService;
    @MockBean
    AccountRepo accountRepo;
    @MockBean
    ConfirmationTokenRepo confirmationTokenRepo;
    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateMockMvc() {
        assertThat(mvc).isNotNull();
    }

    @Test
    void register() throws Exception {
        RegistrationRequest request =
                new RegistrationRequest("Zach", "Warunek", "gfdgbfdgsdf@gmail.com", "password1234");

        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(false);
        mvc.perform(post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration Successful: Email confirmation sent")).andReturn()
                .getResponse();
        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepo).save(accountArgumentCaptor.capture());

        Account capturedAccount = accountArgumentCaptor.getValue();
        assertThat(capturedAccount).usingRecursiveComparison().ignoringFields("password")
                .isEqualTo(new Account(request.getFirstName(),
                                       request.getLastName(),
                                       request.getUsername(),
                                       request.getPassword(),
                                       AccountRole.USER));
        assertThat(passwordEncoder.matches(request.getPassword(), capturedAccount.getPassword())).isTrue();
    }

    @Test
    void registerUsernameTaken() throws Exception {
        RegistrationRequest request =
                new RegistrationRequest("Zach", "Warunek", "gfdgbfdgsdf@gmail.com", "password1234");

        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
        mvc.perform(post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content(asJsonString(request)))
                .andExpect(status().is(409)).andExpect(status().reason(
                        "An account with that email " + request.getUsername() + " " + "already exists")).andReturn()
                .getResponse();
        verify(confirmationTokenRepo, never()).updateConfirmedAt(any(), any());
        verify(accountRepo, never()).enableAccount(any());
    }

    @Test
    void confirm() throws Exception {
        Account account = new Account("Zach", "Warunek", "gfdgbfdgsdf@gmail.com", "password1234", AccountRole.USER);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);

        given(confirmationTokenRepo.findByToken(anyString())).willReturn(Optional.of(confirmationToken));
        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
        System.out.println(asJsonString(account));
        mvc.perform(get("/api/v1/registration/confirm?token=" + confirmationToken.getToken()))
                .andExpect(status().isOk()).andExpect(content().string("Confirmation Successful")).andReturn()
                .getResponse();
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(confirmationTokenRepo).updateConfirmedAt(tokenCaptor.capture(),
                                                        ArgumentCaptor.forClass(LocalDateTime.class).capture());
        verify(accountRepo).enableAccount(usernameCaptor.capture());

        assertThat(tokenCaptor.getValue()).isEqualTo(token);
        assertThat(usernameCaptor.getValue()).isEqualTo(account.getUsername());

    }
}
