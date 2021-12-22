package com.zacharywarunek.amazonclone.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.config.Constants;
import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.UsernameTakenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AccountControllerTest {

  @MockBean AccountService accountService;
  @Autowired private MockMvc mvc;
  @InjectMocks private JwtUtil jwtUtil;
  private Account account;
  private AccountDetails accountDetails;
  private String token;

  public static String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  void setupAccount() {
    account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    accountDetails =
        new AccountDetails("FNChanged", "LNchanged", "Zachdfsdsa@gmail.com", "something");
    token = jwtUtil.generateToken(account);
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
    account2.setId(2L);
    given(accountService.getAllAccounts()).willReturn(Arrays.asList(account1, account2));
    mvc.perform(get("/api/v1/accounts").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(asJsonString(Arrays.asList(account1, account2))))
        .andReturn()
        .getResponse();
  }

  @Test
  void shouldAuthenticate() throws Exception {
    when(accountService.loadUserByUsername(any()))
        .thenReturn(
            new User(account.getUsername(), account.getPassword(), account.getAuthorities()));
    mvc.perform(
            get("/api/v1/apiTestAuth")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", Constants.TOKEN_PREFIX + token))
        .andExpect(status().isOk())
        .andExpect(
            content().string("API Auth is functioning normally for environment: development"))
        .andReturn()
        .getResponse();
  }

  @Test
  void updateAccountWithAuthentication() throws Exception {
    when(accountService.loadUserByUsername(any()))
        .thenReturn(
            new User(account.getUsername(), account.getPassword(), account.getAuthorities()));
    when(accountService.updateAccount(any(), any())).thenReturn(account);
    mvc.perform(
            put("/api/v1/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(accountDetails))
                .header("Authorization", Constants.TOKEN_PREFIX + token))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
        .andExpect(status().reason("Forbidden"));
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void updateAccount() throws Exception {
    when(accountService.updateAccount(any(), any())).thenReturn(account);
    mvc.perform(
            put("/api/v1/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(accountDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string(asJsonString(account)));
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void updateAccountUsernameAlreadyExists() throws Exception {
    when(accountService.updateAccount(any(), any()))
        .thenThrow(new UsernameTakenException("USERNAME TAKEN"));
    mvc.perform(
            put("/api/v1/accounts/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(accountDetails)))
        .andExpect(status().is(HttpStatus.CONFLICT.value()))
        .andExpect(status().reason("USERNAME TAKEN"));
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void updateAccountDoesntExist() throws Exception {
    when(accountService.updateAccount(any(), any()))
        .thenThrow(new EntityNotFoundException("ACCOUNT NOT FOUND"));
    mvc.perform(
            put("/api/v1/accounts/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(accountDetails)))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("ACCOUNT NOT FOUND"));
  }

  @Test
  @WithMockUser(username = "NotZach@gmail.com")
  void updateNotCorrectRoleAndUsername() throws Exception {
    mvc.perform(put("/api/v1/accounts/" + 2).contentType(MediaType.APPLICATION_JSON).content("{}"))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
        .andExpect(status().reason("Forbidden"));
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void deleteAccount() throws Exception {
    mvc.perform(delete("/api/v1/accounts/" + 1).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("Deleted account"));
  }

  @Test
  @WithMockUser(
      username = "Zach@gmail.com",
      roles = {"ADMIN"})
  void deleteAccountNotFound() throws Exception {
    doThrow(new EntityNotFoundException("ACCOUNT NOT FOUND"))
        .when(accountService)
        .deleteAccount(any());
    mvc.perform(delete("/api/v1/accounts/" + 1).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("ACCOUNT NOT FOUND"));
  }
}
