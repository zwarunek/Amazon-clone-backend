package com.zacharywarunek.amazonclone.registration.token;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

  @Mock private ConfirmationTokenRepo confirmationTokenRepo;
  @InjectMocks private ConfirmationTokenService confirmationTokenService;
  private Account account;
  private ConfirmationToken confirmationToken;

  @BeforeEach
  void setUp() {
    account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    confirmationToken =
            new ConfirmationToken(
                    UUID.randomUUID().toString(),
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    account);
  }

  @Test
  void saveToken() {
    confirmationTokenService.saveConfirmationToken(confirmationToken);
    ArgumentCaptor<ConfirmationToken> confirmationTokenCaptor =
        ArgumentCaptor.forClass(ConfirmationToken.class);
    verify(confirmationTokenRepo).save(confirmationTokenCaptor.capture());
    assertThat(confirmationToken).usingRecursiveComparison().isEqualTo(confirmationTokenCaptor.getValue());
  }

  @Test
  void getTokenExists() {
    given(confirmationTokenRepo.findByToken(confirmationToken.getToken())).willReturn(Optional.of(confirmationToken));
    Optional<ConfirmationToken> token = confirmationTokenService.getToken(confirmationToken.getToken());
    ArgumentCaptor<String> tokenCaptor =
            ArgumentCaptor.forClass(String.class);
    verify(confirmationTokenRepo).findByToken(tokenCaptor.capture());
    assertThat(confirmationToken.getToken()).isEqualTo(tokenCaptor.getValue());
    assertThat(token.isPresent()).isTrue();
    assertThat(confirmationToken).isEqualTo(token.get());
  }

  @Test
  void getTokenDoesNotExist() {
    given(confirmationTokenRepo.findByToken(confirmationToken.getToken())).willReturn(Optional.empty());
    Optional<ConfirmationToken> token = confirmationTokenService.getToken(confirmationToken.getToken());
    ArgumentCaptor<String> tokenCaptor =
            ArgumentCaptor.forClass(String.class);
    verify(confirmationTokenRepo).findByToken(tokenCaptor.capture());
    assertThat(confirmationToken.getToken()).isEqualTo(tokenCaptor.getValue());
    assertThat(token.isPresent()).isFalse();
  }

  @Test
  void setConfirmedAt() {
    confirmationTokenService.setConfirmedAt(confirmationToken.getToken());
    ArgumentCaptor<String> tokenCaptor =
            ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<LocalDateTime> timeCaptor =
            ArgumentCaptor.forClass(LocalDateTime.class);
    verify(confirmationTokenRepo).updateConfirmedAt(tokenCaptor.capture(), timeCaptor.capture());
    assertThat(confirmationToken.getToken()).isEqualTo(tokenCaptor.getValue());
  }

  @Test
  void deleteAllAtAccount() {
    confirmationTokenService.deleteAllAtAccount(account);
    ArgumentCaptor<Account> accountCaptor =
            ArgumentCaptor.forClass(Account.class);
    verify(confirmationTokenRepo).deleteAllByAccountId(accountCaptor.capture());
    assertThat(account).isEqualTo(accountCaptor.getValue());
  }
}
