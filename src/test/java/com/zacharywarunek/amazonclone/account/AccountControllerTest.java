package com.zacharywarunek.amazonclone.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.config.JwtFilter;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenRepo;
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

  @Autowired AccountService accountService;
  @MockBean AccountRepo accountRepo;
  @Autowired JwtFilter jwtFilter;
  @Autowired private MockMvc mvc;
  @MockBean private ConfirmationTokenRepo confirmationTokenRepo;
  @InjectMocks private BCryptPasswordEncoder passwordEncoder;

  public static String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void shouldCreateMockMvc() {
    assertNotNull(mvc);
  }

  @Test
  @WithMockUser(
      username = "test@gmail.com",
      roles = {"ADMIN"})
  void getAllAccounts() throws Exception {
    Account account1 =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    Account account2 =
        new Account("Zach2", "Warunek2", "Zach@gmail.com2", "password12342", AccountRole.ROLE_USER);
    account1.setId(1L);
    given(accountRepo.findAccountByUsername(account1.getUsername()))
        .willReturn(java.util.Optional.of(account1));
    given(accountRepo.findAll()).willReturn(Arrays.asList(account1, account2));

    mvc.perform(get("/api/v1/accounts").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(asJsonString(Arrays.asList(account1, account2))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void updateAccount() throws Exception {
    Account account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password", AccountRole.ROLE_USER);
    account.setId(1L);
    Map<String, String> accountDetails = new HashMap<>();
    accountDetails.put("first_name", "Zachary");
    accountDetails.put("username", "changedEmail");
    accountDetails.put("password", "newPassword");
    given(accountRepo.findAccountByUsername(account.getUsername()))
        .willReturn(java.util.Optional.of(account));
    given(accountRepo.findById(1L)).willReturn(java.util.Optional.of(account));

    mvc.perform(
            put("/api/v1/accounts/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(accountDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string("Updated account"));

    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

    verify(accountRepo).save(accountCaptor.capture());
    if (accountDetails.containsKey("first_name"))
      assertEquals(accountCaptor.getValue().getFirst_name(), accountDetails.get("first_name"));
    if (accountDetails.containsKey("last_name"))
      assertNotEquals(accountCaptor.getValue().getLast_name(), accountDetails.get("last_name"));
    if (accountDetails.containsKey("username"))
      assertEquals(accountCaptor.getValue().getUsername(), accountDetails.get("username"));
    if (accountDetails.containsKey("password"))
      assertTrue(
          passwordEncoder.matches(
              accountDetails.get("password"), accountCaptor.getValue().getPassword()));

    assertNotEquals(
        account.toString(),
        new Account(
                accountDetails.getOrDefault("first_name", null),
                accountDetails.getOrDefault("last_name", null),
                accountDetails.getOrDefault("username", null),
                accountDetails.getOrDefault("password", null),
                null)
            .toString());
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void updateAccountUsernameAlreadyExists() throws Exception {
    Account account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    account.setId(1L);
    Map<String, String> accountDetails = new HashMap<>();
    accountDetails.put("first_name", "Zachary");
    accountDetails.put("username", "changedEmail");
    given(accountRepo.findById(1L)).willReturn(java.util.Optional.of(account));
    given(accountRepo.findAccountByUsername(account.getUsername()))
        .willReturn(java.util.Optional.of(account));
    given(accountRepo.findAccountByUsername(accountDetails.get("username")))
        .willReturn(java.util.Optional.of(account));

    mvc.perform(
            put("/api/v1/accounts/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(accountDetails)))
        .andExpect(status().is(HttpStatus.CONFLICT.value()))
        .andExpect(status().reason("Username is already in use"));
    verify(accountRepo, never()).save(any());
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void updateAccountDoesntExist() throws Exception {
    Account account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    account.setId(1L);
    given(accountRepo.findAccountByUsername(account.getUsername()))
        .willReturn(java.util.Optional.of(account));
    given(accountRepo.findById(1L)).willReturn(java.util.Optional.empty());
    mvc.perform(put("/api/v1/accounts/" + 1).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("Account with id " + 1 + " doesn't exist"));
    verify(accountRepo, never()).save(any());
  }

  @Test
  @WithMockUser(username = "NotZach@gmail.com")
  void updateNotCorrectRoleAndUsername() throws Exception {
    Account account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    account.setId(1L);
    given(accountRepo.findAccountByUsername("NotZach@gmail.com"))
        .willReturn(java.util.Optional.of(account));
    mvc.perform(put("/api/v1/accounts/" + 2).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
        .andExpect(status().reason("Forbidden"));
    verify(accountRepo, never()).save(any());
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void deleteAccount() throws Exception {
    String password = "password1234";
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "Zach@gmail.com",
            passwordEncoder.encode(password),
            AccountRole.ROLE_USER);
    account.setId(1L);
    given(accountRepo.findById(1L)).willReturn(java.util.Optional.of(account));
    given(accountRepo.findAccountByUsername(account.getUsername()))
        .willReturn(java.util.Optional.of(account));

    mvc.perform(delete("/api/v1/accounts/" + 1).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Deleted account"));

    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    ArgumentCaptor<Long> accountIdCaptor = ArgumentCaptor.forClass(Long.class);
    verify(confirmationTokenRepo).deleteAllByAccountId(accountCaptor.capture());
    verify(accountRepo).deleteById(accountIdCaptor.capture());

    assertEquals(account, accountCaptor.getValue());
    assertEquals(account.getId(), accountIdCaptor.getValue());
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void deleteAccountNotFound() throws Exception {
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "Zach@gmail.com",
            passwordEncoder.encode("password"),
            AccountRole.ROLE_USER);
    account.setId(1L);
    given(accountRepo.findAccountByUsername(account.getUsername()))
        .willReturn(java.util.Optional.of(account));
    given(accountRepo.findById(1)).willReturn(java.util.Optional.empty());
    mvc.perform(delete("/api/v1/accounts/" + 1).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("Account with id " + 1 + " doesn't exist"));
    verify(confirmationTokenRepo, never()).deleteAllByAccountId(any());
    verify(accountRepo, never()).deleteById(any());
  }
}
