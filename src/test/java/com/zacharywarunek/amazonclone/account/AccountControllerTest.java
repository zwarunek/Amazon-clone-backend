package com.zacharywarunek.amazonclone.account;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.config.JwtUtil;
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
import org.springframework.mock.web.MockHttpServletResponse;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AccountControllerTest {

    @Autowired
    AccountService accountService;
    @MockBean
    AccountRepo accountRepo;
    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private JwtUtil jwtUtil;
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

    public static JsonNode jsonStringToMap(final String str) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(str);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void getAllAccounts() throws Exception {
        Account account1 = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.USER);
        Account account2 = new Account("Zach2", "Warunek2", "Zach@gmail.com2", "password12342", AccountRole.USER);
        given(accountRepo.findAll()).willReturn(Arrays.asList(account1, account2));

        MockHttpServletResponse response =
                mvc.perform(get("/api/v1/account").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentAsString(), asJsonString(Arrays.asList(account1, account2)));
    }

    @Test
    void authenticate() throws Exception {

        String password = "password1234";
        Account account =
                new Account("Zach", "Warunek", "Zach@gmail.com", passwordEncoder.encode(password), AccountRole.USER);
        AuthRequest authRequest = new AuthRequest("Zach@gmail.com", password);
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.of(account));

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/account/authenticate").contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authRequest))).andReturn().getResponse();
        assertEquals(authRequest.getUsername(),
                jwtUtil.getUsernameFromToken(response.getHeaders("Authorization").get(0)));
        assertEquals(response.getStatus(), HttpStatus.OK.value());
    }

    @Test
    void authenticateUsernameNotFound() throws Exception {

        AuthRequest authRequest = new AuthRequest("NotInDB@gmail.com", "password1234");
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.empty());

        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/account/authenticate").contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authRequest))).andReturn().getResponse();
        assertEquals("Username or Password was incorrect", response.getErrorMessage());
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void authenticateFieldsNotFilled() throws Exception {
        AuthRequest authRequest = new AuthRequest(null, "password");
        MockHttpServletResponse response = mvc.perform(
                post("/api/v1/account/authenticate").contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(authRequest))).andReturn().getResponse();

        assertEquals("'username' or 'password' fields not found", response.getErrorMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void updateAccount() throws Exception {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password", AccountRole.USER);
        Map<String, String> accountDetails = new HashMap<>();
        accountDetails.put("first_name", "Zachary");
        accountDetails.put("username", "changedEmail");
        accountDetails.put("password", "newPassword");
        given(accountRepo.findById(1)).willReturn(java.util.Optional.of(account));


        MockHttpServletResponse response = mvc.perform(
                put("/api/v1/account/" + 1).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(accountDetails))).andReturn().getResponse();

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepo).save(accountCaptor.capture());
        if(accountDetails.containsKey("first_name"))
            assertEquals(accountCaptor.getValue().getFirst_name(), accountDetails.get("first_name"));
        if(accountDetails.containsKey("last_name"))
            assertNotEquals(accountCaptor.getValue().getLast_name(), accountDetails.get("last_name"));
        if(accountDetails.containsKey("username"))
            assertEquals(accountCaptor.getValue().getUsername(), accountDetails.get("username"));
        if(accountDetails.containsKey("password")) assertTrue(
                passwordEncoder.matches(accountDetails.get("password"),
                        accountCaptor.getValue().getPassword()));

        assertNotEquals(account.toString(), new Account(accountDetails.getOrDefault("first_name", null),
                accountDetails.getOrDefault("last_name", null), accountDetails.getOrDefault("username", null),
                accountDetails.getOrDefault("password", null), null).toString());

        assertEquals("Updated Account", jsonStringToMap(response.getContentAsString()).get("message").asText());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void updateAccountUsernameAlreadyExists() throws Exception {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.USER);
        Map<String, Object> accountDetails = new HashMap<>();
        accountDetails.put("first_name", "Zachary");
        accountDetails.put("username", "changedEmail");
        given(accountRepo.findById(1)).willReturn(java.util.Optional.of(account));
        given(accountRepo.findAccountByUsername(accountDetails.get("username").toString())).willReturn(
                java.util.Optional.of(account));

        MockHttpServletResponse response = mvc.perform(
                put("/api/v1/account/" + 1).contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(accountDetails))).andReturn().getResponse();

        assertEquals("Username is already in use", response.getErrorMessage());
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        verify(accountRepo, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void updateAccountDoesntExist() throws Exception {
        Account accountDetails = new Account("ZachChange", "WarunekChange", "Zach@gmail.comChange", null, null);
        given(accountRepo.findById(1)).willReturn(java.util.Optional.empty());
        MockHttpServletResponse response =
                mvc.perform(put("/api/v1/account/" + 1).contentType(MediaType.APPLICATION_JSON).content("{}"))
                        .andReturn().getResponse();
        assertEquals("Account with id " + 1 + " doesn't exist", response.getErrorMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        verify(accountRepo, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void deleteAccount() throws Exception {
        String password = "password1234";
        Account account =
                new Account("Zach", "Warunek", "Zach@gmail.com", passwordEncoder.encode(password), AccountRole.USER);
        account.setId(1);
        given(accountRepo.findById(account.getId())).willReturn(java.util.Optional.of(account));

        MockHttpServletResponse response =
                mvc.perform(delete("/api/v1/account/" + account.getId()).contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<Integer> accountIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(confirmationTokenService).deleteAllAtAccountId(accountCaptor.capture());
        verify(accountRepo).deleteById(accountIdCaptor.capture());

        assertEquals(account, accountCaptor.getValue());
        assertEquals(account.getId(), accountIdCaptor.getValue());

        assertEquals(HttpStatus.OK.value(), response.getStatus());

    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void deleteAccountNotFound() throws Exception {
        given(accountRepo.findById(1)).willReturn(java.util.Optional.empty());

        MockHttpServletResponse response =
                mvc.perform(delete("/api/v1/account/" + 1).contentType(MediaType.APPLICATION_JSON)).andReturn()
                        .getResponse();
        verify(confirmationTokenService, never()).deleteAllAtAccountId(any());
        verify(accountRepo, never()).deleteById(any());
        assertEquals("Account with id " + 1 + " doesn't exist", response.getErrorMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }
}
