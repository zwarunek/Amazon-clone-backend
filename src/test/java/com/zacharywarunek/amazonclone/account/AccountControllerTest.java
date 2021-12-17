package com.zacharywarunek.amazonclone.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.config.JwtFilter;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import com.zacharywarunek.amazonclone.util.AuthRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AccountControllerTest {

    @Autowired
    AccountService accountService;
    @MockBean
    AccountRepo accountRepo;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ConfirmationTokenService confirmationTokenService;
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
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void getAllAccounts() throws Exception {
        Account account1 = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
        Account account2 = new Account("Zach2", "Warunek2", "Zach@gmail.com2", "password12342", AccountRole.ROLE_USER);
        given(accountRepo.findAll()).willReturn(Arrays.asList(account1, account2));

        mvc.perform(get("/api/v1/account").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().string(asJsonString(Arrays.asList(account1, account2)))).andReturn().getResponse();
    }

    @Test
    void authenticate() throws Exception {

        String password = "password1234";
        Account account =
                new Account("Zach", "Warunek", "Zach@gmail.com", passwordEncoder.encode(password), AccountRole.ROLE_USER);
        AuthRequest authRequest = new AuthRequest("Zach@gmail.com", password);
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.of(account));

        mvc.perform(post("/api/v1/account/authenticate").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(authRequest))).andExpect(status().isOk())
                .andExpect(content().string("Authorization Successful"));
    }

    @Test
    void authenticateUsernameNotFound() throws Exception {

        AuthRequest authRequest = new AuthRequest("NotInDB@gmail.com", "password1234");
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.empty());

        mvc.perform(post("/api/v1/account/authenticate").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(authRequest))).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(status().reason("Username or Password was incorrect"));
    }

    @Test
    void authenticateFieldsNotFilled() throws Exception {
        AuthRequest authRequest = new AuthRequest(null, "password");
        mvc.perform(post("/api/v1/account/authenticate").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(authRequest))).andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(status().reason("'username' or 'password' fields not found"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateAccount() throws Exception {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password", AccountRole.ROLE_USER);
        Map<String, String> accountDetails = new HashMap<>();
        accountDetails.put("first_name", "Zachary");
        accountDetails.put("username", "changedEmail");
        accountDetails.put("password", "newPassword");
        given(accountRepo.findById(1)).willReturn(java.util.Optional.of(account));

        mvc.perform(put("/api/v1/account/" + 1).contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(accountDetails))).andExpect(status().isOk())
                .andExpect(content().string("Updated Account"));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepo).save(accountCaptor.capture());
        if(accountDetails.containsKey("first_name"))
            assertEquals(accountCaptor.getValue().getFirst_name(), accountDetails.get("first_name"));
        if(accountDetails.containsKey("last_name"))
            assertNotEquals(accountCaptor.getValue().getLast_name(), accountDetails.get("last_name"));
        if(accountDetails.containsKey("username"))
            assertEquals(accountCaptor.getValue().getUsername(), accountDetails.get("username"));
        if(accountDetails.containsKey("password"))
            assertTrue(passwordEncoder.matches(accountDetails.get("password"), accountCaptor.getValue().getPassword()));

        assertNotEquals(account.toString(),
                        new Account(accountDetails.getOrDefault("first_name", null),
                                    accountDetails.getOrDefault("last_name", null),
                                    accountDetails.getOrDefault("username", null),
                                    accountDetails.getOrDefault("password", null),
                                    null).toString());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateAccountUsernameAlreadyExists() throws Exception {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
        Map<String, Object> accountDetails = new HashMap<>();
        accountDetails.put("first_name", "Zachary");
        accountDetails.put("username", "changedEmail");
        given(accountRepo.findById(1)).willReturn(java.util.Optional.of(account));
        given(accountRepo.findAccountByUsername(accountDetails.get("username")
                                                        .toString())).willReturn(java.util.Optional.of(account));
        mvc.perform(put("/api/v1/account/" + 1).contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(accountDetails))).andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(status().reason("Username is already in use"));
        verify(accountRepo, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void updateAccountDoesntExist() throws Exception {
        given(accountRepo.findById(1)).willReturn(java.util.Optional.empty());
        mvc.perform(put("/api/v1/account/" + 1).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(status().reason("Account with id " + 1 + " doesn't exist"));
        verify(accountRepo, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteAccount() throws Exception {
        String password = "password1234";
        Account account =
                new Account("Zach", "Warunek", "Zach@gmail.com", passwordEncoder.encode(password), AccountRole.ROLE_USER);
        account.setId(1);
        given(accountRepo.findById(account.getId())).willReturn(java.util.Optional.of(account));

        mvc.perform(delete("/api/v1/account/" + account.getId()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string("Deleted Account"));

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<Integer> accountIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(confirmationTokenService).deleteAllAtAccountId(accountCaptor.capture());
        verify(accountRepo).deleteById(accountIdCaptor.capture());

        assertEquals(account, accountCaptor.getValue());
        assertEquals(account.getId(), accountIdCaptor.getValue());

    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    void deleteAccountNotFound() throws Exception {
        given(accountRepo.findById(1)).willReturn(java.util.Optional.empty());
        mvc.perform(delete("/api/v1/account/" + 1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(status().reason("Account with id " + 1 + " doesn't exist"));
        verify(confirmationTokenService, never()).deleteAllAtAccountId(any());
        verify(accountRepo, never()).deleteById(any());
    }
}
