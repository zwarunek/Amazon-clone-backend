package com.zacharywarunek.amazonclone.registration;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.email.EmailService;
import com.zacharywarunek.amazonclone.exceptions.*;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationToken;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

  @Mock private AccountRepo accountRepo;
  @Mock private ConfirmationTokenService confirmationTokenService;
  @Mock private AccountService accountService;
  @Mock private EmailService emailService;
  @InjectMocks private RegistrationService registrationService;
  private ConfirmationToken confirmationToken;
  private String token;

  @BeforeEach
  void setupAccount() {
    Account account = new Account("Zach",
                                  "Warunek",
                                  "Zach@gmail.com",
                                  "password1234",
                                  AccountRole.ROLE_USER);
    token = UUID.randomUUID().toString();
    confirmationToken =
        new ConfirmationToken(
                token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15),
                account);
  }

  @Test
  void register() throws BadRequestException, UsernameTakenException {
    RegistrationRequest request =
        new RegistrationRequest("Zach", "Warunek", "Zach@gmail.com", "password1234");
    String mockToken = "MOCKLINK";
    String mockLink = System.getenv("URL") + "/api/v1/registration/confirm?token=" + mockToken;
    given(accountService.register(any())).willReturn(mockToken);
    String link = registrationService.register(request);

    assertThat(link).isEqualTo(mockLink);
  }

  @Test
  void confirm()
      throws InvalidTokenException, ExpiredTokenException, BadRequestException,
          EntityNotFoundException {
    given(confirmationTokenService.getToken(token)).willReturn(Optional.of(confirmationToken));
    given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
    ConfirmationToken actualToken = registrationService.confirmToken(token);
    assertThat(actualToken).isEqualTo(confirmationToken);
  }

  @Test
  void alreadyConfirmed() {
    confirmationToken.setConfirmed_at(LocalDateTime.now().plusMinutes(5));
    given(confirmationTokenService.getToken(token)).willReturn(Optional.of(confirmationToken));
    given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
    assertThatThrownBy(() -> registrationService.confirmToken(token))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(EMAIL_ALREADY_CONFIRMED.label);
  }

  @Test
  void tokenNotFound() {
    given(confirmationTokenService.getToken(token)).willReturn(Optional.empty());
    assertThatThrownBy(() -> registrationService.confirmToken(token))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessage(INVALID_TOKEN.label);
  }

  @Test
  void invalidCreateTime() {
    confirmationToken.setCreated_at(LocalDateTime.now().plusMinutes(5));
    given(confirmationTokenService.getToken(token)).willReturn(Optional.of(confirmationToken));
    assertThatThrownBy(() -> registrationService.confirmToken(token))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessage(INVALID_TOKEN.label);
  }

  @Test
  void usernameNotFound() {
    given(confirmationTokenService.getToken(token)).willReturn(Optional.of(confirmationToken));
    given(accountRepo.checkIfUsernameExists(anyString())).willReturn(false);
    assertThatThrownBy(() -> registrationService.confirmToken(token))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(
            String.format(USERNAME_NOT_FOUND.label, confirmationToken.getAccount().getUsername()));
  }

  @Test
  void expiredToken() {
    confirmationToken.setExpires_at(LocalDateTime.now().minusMinutes(15));
    confirmationToken.setCreated_at(LocalDateTime.now().minusMinutes(30));
    given(confirmationTokenService.getToken(token)).willReturn(Optional.of(confirmationToken));
    given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);
    assertThatThrownBy(() -> registrationService.confirmToken(token))
        .isInstanceOf(ExpiredTokenException.class)
        .hasMessage(EXPIRED_TOKEN.label);
  }
}
