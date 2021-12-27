package com.zacharywarunek.amazonclone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.config.Constants;
import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import com.zacharywarunek.amazonclone.util.AuthRequest;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.NULL_VALUES;
import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.USERNAME_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class BaseControllerTest {

  @MockBean AccountService accountService;
  @Autowired private MockMvc mvc;
  @InjectMocks private JwtUtil jwtUtil;
  private Account account;
  private AuthRequest authRequest;
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
    authRequest = new AuthRequest("Zachdfsdsa@gmail.com", "something");
    token = jwtUtil.generateToken(account);
  }

  @Test
  @WithMockUser(
      username = "test@gmail.com",
      roles = {"ADMIN"})
  void apiTest() throws Exception {

    mvc.perform(get("/api/v1/apiTest").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("API is functioning normally for environment: development"))
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
  void authenticationUserNotFound() throws Exception {
    when(accountService.loadUserByUsername(any()))
        .thenThrow(
            new UsernameNotFoundException(
                String.format(USERNAME_NOT_FOUND.name(), account.getUsername())));
    mvc.perform(
            get("/api/v1/apiTestAuth")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Constants.TOKEN_PREFIX + token))
        .andExpect(status().is(404))
        .andExpect(status().reason(String.format(USERNAME_NOT_FOUND.name(), account.getUsername())))
        .andReturn()
        .getResponse();
  }

  @Test
  void authenticationTokenExpired() throws Exception {
    String expriredToken =
        "eyJhbGciOiJIUzI1NiJ9"
            + ".eyJzdWIiOiJ6QGdtYWlsLmNvbSIsInNjb3BlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjM5NjMxOTcwLCJleHAiOjE2Mzk2MzE5NzB9.xzjemMsq53NobQM5j3xZVY5s9s5z-1zT5aHFSNVuW9U";
    mvc.perform(
            put("/api/v1/apiTestAuth" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Constants.TOKEN_PREFIX + expriredToken))
        .andExpect(status().is(401))
        .andExpect(status().reason("Unauthorized"))
        .andReturn()
        .getResponse();
  }

  @Test
  void authenticationInvalidCombo() throws Exception {
    mvc.perform(
            put("/api/v1/apiTestAuth" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", Constants.TOKEN_PREFIX + "randomString"))
        .andExpect(status().is(401))
        .andExpect(status().reason("Unauthorized"))
        .andReturn()
        .getResponse();
  }

  @Test
  void authenticate() throws Exception {
    when(accountService.authenticate(authRequest)).thenReturn(token);
    mvc.perform(
            post("/api/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authRequest)))
        .andExpect(status().isOk())
        .andExpect(content().string("Authentication Successful"))
        .andExpect(header().string("Authorization", token));
  }

  @Test
  void authenticateUsernamePasswordDontMatch() throws Exception {
    when(accountService.authenticate(authRequest)).thenThrow(new UnauthorizedException("Unauthorized"));
    mvc.perform(
            post("/api/v1/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(authRequest)))
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
        .andExpect(status().reason("Unauthorized"));
  }

  @Test
  void authenticateUsernameNotFound() throws Exception {
    when(accountService.authenticate(authRequest)).thenThrow(new BadRequestException(NULL_VALUES.label));
    mvc.perform(
                    post("/api/v1/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(authRequest)))
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andExpect(status().reason(NULL_VALUES.label));
  }
}
