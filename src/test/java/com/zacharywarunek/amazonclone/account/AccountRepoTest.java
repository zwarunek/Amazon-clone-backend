package com.zacharywarunek.amazonclone.account;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AccountRepoTest {

  @Autowired private AccountRepo accountRepo;
  private Account account;

  @BeforeEach
  void setup() {
    account =
        new Account(
            "Zach",
            "Warunek",
            "Zach@gmail.com",
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
  }

  @AfterEach
  void tearDown() {
    accountRepo.deleteAll();
  }

  @Test
  void findAccountByEmailShouldBeTrue() {
    Optional<Account> accountOptional = accountRepo.findAccountByUsername(account.getUsername());
    assertThat(accountOptional.isPresent()).isTrue();
    assertThat(accountOptional.get()).isEqualTo(account);
  }

  @Test
  void findAccountByEmailShouldBeFalse() {
    Optional<Account> accountOptional =
        accountRepo.findAccountByUsername("notInDatabase@gmail.com");
    assertThat(accountOptional.isPresent()).isFalse();
  }

  @Test
  void checkIfEmailExistsShouldBeTrue() {
    assertThat(accountRepo.checkIfUsernameExists(account.getUsername())).isTrue();
  }

  @Test
  void checkIfEmailExistsShouldBeFalse() {
    assertThat(accountRepo.checkIfUsernameExists("notInDatabase@gmail.com")).isFalse();
  }

  @Test
  void enableAccount() {
    accountRepo.enableAccount(account.getUsername());

    Optional<Account> accountOptional = accountRepo.findById(account.getId());
    assertThat(accountOptional.isPresent()).isTrue();
    assertThat(accountOptional.get().isEnabled()).isTrue();
  }
}
