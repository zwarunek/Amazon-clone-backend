package com.zacharywarunek.amazonclone.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.exceptions.*;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationToken;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenRepo;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class RegistrationControllerTest {

  @MockBean AccountService accountService;
  @MockBean ConfirmationTokenService confirmationTokenService;
  @MockBean RegistrationService registrationService;
  @MockBean AccountRepo accountRepo;
  @MockBean ConfirmationTokenRepo confirmationTokenRepo;
  @Autowired private MockMvc mvc;
  private RegistrationRequest request;
  private String link;
  private String token;
  private ConfirmationToken confirmationToken;

  public static String toJson(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      mapper.findAndRegisterModules();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  void setupAccount() {
    request = new RegistrationRequest("Zach", "Warunek", "Zach@gmail.com", "password1234");

    Account account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    link = "MOCK LINK";
    token = UUID.randomUUID().toString();
    confirmationToken =
        new ConfirmationToken(
            token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
  }

  @Test
  void shouldCreateMockMvc() {
    assertThat(mvc).isNotNull();
  }

  @Test
  void register() throws Exception {
    given(registrationService.register(any())).willReturn(link);
    mvc.perform(
            post("/api/v1/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isOk())
        .andExpect(content().string(link))
        .andReturn()
        .getResponse();
  }

  @Test
  void registerUsernameTaken() throws Exception {
    when(registrationService.register(any()))
        .thenThrow(new UsernameTakenException("MOCK USERNAME TAKEN EXCEPTION"));
    mvc.perform(
            post("/api/v1/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is(HttpStatus.CONFLICT.value()))
        .andExpect(status().reason("MOCK USERNAME TAKEN EXCEPTION"))
        .andReturn()
        .getResponse();
  }

  @Test
  void registerNullValues() throws Exception {
    when(registrationService.register(any()))
        .thenThrow(new BadRequestException("NULL VALUES MOCK"));
    mvc.perform(
            post("/api/v1/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(status().reason("NULL VALUES MOCK"))
        .andReturn()
        .getResponse();
  }

  @Test
  void confirm() throws Exception {
    given(registrationService.confirmToken(token)).willReturn(confirmationToken);
    mvc.perform(
            get("/api/v1/registration/confirm?token=" + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse();
  }

  @Test
  void alreadyConfirmed() throws Exception {
    when(registrationService.confirmToken(token))
        .thenThrow(new BadRequestException("ACCOUNT ALREADY CONFIRMED"));
    mvc.perform(
            get("/api/v1/registration/confirm?token=" + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(status().reason("ACCOUNT ALREADY CONFIRMED"))
        .andReturn()
        .getResponse();
  }

  @Test
  void invalidToken() throws Exception {
    when(registrationService.confirmToken(token))
        .thenThrow(new InvalidTokenException("INVALID TOKEN"));
    mvc.perform(
            get("/api/v1/registration/confirm?token=" + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
        .andExpect(status().reason("INVALID TOKEN"))
        .andReturn()
        .getResponse();
  }

  @Test
  void expiredToken() throws Exception {
    when(registrationService.confirmToken(token))
        .thenThrow(new ExpiredTokenException("EXPIRED TOKEN"));
    mvc.perform(
            get("/api/v1/registration/confirm?token=" + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
        .andExpect(status().reason("EXPIRED TOKEN"))
        .andReturn()
        .getResponse();
  }

  @Test
  void tokenNotFound() throws Exception {
    when(registrationService.confirmToken(token))
        .thenThrow(new EntityNotFoundException("NOT FOUND"));
    mvc.perform(
            get("/api/v1/registration/confirm?token=" + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("NOT FOUND"))
        .andReturn()
        .getResponse();
  }
}
