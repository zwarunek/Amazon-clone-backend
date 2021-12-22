package com.zacharywarunek.amazonclone.registration.token;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConfirmationTokenRepoTest {

  @Autowired private ConfirmationTokenRepo confirmationTokenRepo;
  @Autowired private AccountRepo accountRepo;

  @Test
  void findByToken() {
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "Zach@gmail.com",
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken =
        new ConfirmationToken(
            token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
    confirmationTokenRepo.save(confirmationToken);
    Optional<ConfirmationToken> confirmationTokenOptional =
        confirmationTokenRepo.findByToken(token);
    assertThat(confirmationTokenOptional.isPresent()).isTrue();
    assertThat(confirmationTokenOptional.get()).isEqualTo(confirmationToken);
  }

  @Test
  void findByTokenNotFound() {
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "Zach@gmail.com",
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken =
        new ConfirmationToken(
            token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
    confirmationTokenRepo.save(confirmationToken);
    Optional<ConfirmationToken> confirmationTokenOptional =
        confirmationTokenRepo.findByToken(UUID.randomUUID().toString());
    assertThat(confirmationTokenOptional.isPresent()).isFalse();
  }

  @Test
  void updateConfirmedAt() {
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "Zach@gmail.com",
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken =
        new ConfirmationToken(
            token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
    confirmationTokenRepo.save(confirmationToken);
    LocalDateTime now = LocalDateTime.now();
    confirmationTokenRepo.updateConfirmedAt(token, now);

    Optional<ConfirmationToken> tokenOptional =
        confirmationTokenRepo.findById(confirmationToken.getId());
    assertThat(tokenOptional.isPresent()).isTrue();
    assertThat(
            tokenOptional
                .get()
                .getConfirmed_at()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        .isEqualTo(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  }

  @Test
  void deleteAllByAccountId() {
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "Zach@gmail.com",
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken =
        new ConfirmationToken(
            token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
    confirmationTokenRepo.save(confirmationToken);
    confirmationTokenRepo.deleteAllByAccountId(account);
    List<ConfirmationToken> tokens = confirmationTokenRepo.findAll();
    assertThat(tokens.isEmpty()).isTrue();
    assertThat(confirmationTokenRepo.findByToken(token).isPresent()).isFalse();
  }
}
