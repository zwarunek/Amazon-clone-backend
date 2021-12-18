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
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
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
    @Mock
    JavaMailSender mailSender;
    @MockBean
    ConfirmationTokenRepo confirmationTokenRepo;
    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;

    public static String toJson(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
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
        RegistrationRequest request = new RegistrationRequest("Zach", "Warunek", "Zach@gmail.com", "password1234");

        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(false);
        mvc.perform(post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
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
                                       AccountRole.ROLE_USER));
        assertThat(passwordEncoder.matches(request.getPassword(), capturedAccount.getPassword())).isTrue();
    }

    @Test
    void registerUsernameTaken() throws Exception {
        RegistrationRequest request = new RegistrationRequest("Zach", "Warunek", "Zach@gmail.com", "password1234");

        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
        mvc.perform(post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().is(409)).andExpect(status().reason(
                        "An account with that email " + request.getUsername() + " " + "already exists")).andReturn()
                .getResponse();
        verify(confirmationTokenRepo, never()).updateConfirmedAt(any(), any());
        verify(accountRepo, never()).enableAccount(any());
    }

    @Test
    void registerNullValues() throws Exception {
        RegistrationRequest request = new RegistrationRequest("Zach", "Warunek", null, null);

        mvc.perform(post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().is(400))
                .andExpect(status().reason("An error occurred when creating the account: Null values present"))
                .andReturn().getResponse();
        verify(accountRepo, never()).checkIfUsernameExists(anyString());
        verify(confirmationTokenRepo, never()).updateConfirmedAt(any(), any());
        verify(accountRepo, never()).enableAccount(any());
    }

    @Test
    void registerEmailError() throws Exception {
        RegistrationRequest request = new RegistrationRequest("Zach", "Warunek", "@INVALID_EMAIL", "password1234");

        mvc.perform(post("/api/v1/registration").contentType(MediaType.APPLICATION_JSON).content(toJson(request)))
                .andExpect(status().isOk()).andReturn().getResponse();
    }

    @Test
    void confirm() throws Exception {
        Account account =
                new Account("Zach", "Warunek", "gfdgbfdgsdf@gmail.com", "password1234", AccountRole.ROLE_USER);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);

        given(confirmationTokenRepo.findByToken(anyString())).willReturn(Optional.of(confirmationToken));
        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
        mvc.perform(get("/api/v1/registration/confirm?token=" + confirmationToken.getToken()))
                .andExpect(status().isOk())
                .andExpect(content().string("Confirmation Successful: Account has been confirmed")).andReturn()
                .getResponse();
        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(confirmationTokenRepo).updateConfirmedAt(tokenCaptor.capture(),
                                                        ArgumentCaptor.forClass(LocalDateTime.class).capture());
        verify(accountRepo).enableAccount(usernameCaptor.capture());

        assertThat(tokenCaptor.getValue()).isEqualTo(token);
        assertThat(usernameCaptor.getValue()).isEqualTo(account.getUsername());

    }

    @Test
    void accountAlreadyConfirmed() throws Exception {
        Account account =
                new Account("Zach", "Warunek", "gfdgbfdgsdf@gmail.com", "password1234", AccountRole.ROLE_USER);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
        confirmationToken.setConfirmed_at(LocalDateTime.now());

        given(confirmationTokenRepo.findByToken(anyString())).willReturn(Optional.of(confirmationToken));
        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
        mvc.perform(get("/api/v1/registration/confirm?token=" + confirmationToken.getToken()))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(status().reason("email already confirmed")).andReturn().getResponse();

        verify(confirmationTokenRepo, never()).updateConfirmedAt(any(), any());
        verify(accountRepo, never()).enableAccount(any());

    }

    @Test
    void confirmationTokenUserNotFound() throws Exception {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);

        given(confirmationTokenRepo.findByToken(anyString())).willReturn(Optional.of(confirmationToken));
        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(false);
        mvc.perform(get("/api/v1/registration/confirm?token=" + confirmationToken.getToken()))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(status().reason("Account with username Zach@gmail.com doesn't exist")).andReturn()
                .getResponse();

        verify(confirmationTokenRepo, never()).updateConfirmedAt(any(), any());
        verify(accountRepo, never()).enableAccount(any());

    }

    @Test
    void confirmationTokenInvalid() throws Exception {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now().plusMinutes(15), LocalDateTime.now().plusMinutes(30),
                                      account);

        given(confirmationTokenRepo.findByToken(anyString())).willReturn(Optional.of(confirmationToken));
        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(false);
        mvc.perform(get("/api/v1/registration/confirm?token=" + confirmationToken.getToken()))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(status().reason("token is invalid")).andReturn()
                .getResponse();

        verify(confirmationTokenRepo, never()).updateConfirmedAt(any(), any());
        verify(accountRepo, never()).enableAccount(any());

    }

    @Test
    void accountTokenExpired() throws Exception {
        Account account =
                new Account("Zach", "Warunek", "gfdgbfdgsdf@gmail.com", "password1234", AccountRole.ROLE_USER);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().minusMinutes(15), account);

        given(confirmationTokenRepo.findByToken(anyString())).willReturn(Optional.of(confirmationToken));
        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
        mvc.perform(get("/api/v1/registration/confirm?token=" + confirmationToken.getToken()))
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value())).andExpect(status().reason("token is expired"))
                .andReturn().getResponse();

        verify(confirmationTokenRepo, never()).updateConfirmedAt(any(), any());
        verify(accountRepo, never()).enableAccount(any());

    }
}
