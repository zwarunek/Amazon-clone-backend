package com.zacharywarunek.amazonclone.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepoTest {

  @Autowired private AccountRepo accountRepo;

  @Test
  void findAccountByEmailShouldBeTrue() {
    String email = "Zach@gmail.com";
    Account account =
        new Account(
            "Zach",
            "Warunek",
            email,
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    Optional<Account> accountOptional = accountRepo.findAccountByUsername(email);
    assertThat(accountOptional.isPresent()).isTrue();
    assertThat(accountOptional.get()).isEqualTo(account);
  }

  @Test
  void findAccountByEmailShouldBeFalse() {
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "Zach@gmail.com",
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    Optional<Account> accountOptional =
        accountRepo.findAccountByUsername("notInDatabase@gmail.com");
    assertThat(accountOptional.isPresent()).isFalse();
  }

  @Test
  void checkIfEmailExistsShouldBeTrue() {
    String email = "za@gmail.com";
    Account account =
        new Account(
            "Zach",
            "Warunek",
            email,
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    assertThat(accountRepo.checkIfUsernameExists(email)).isTrue();
  }

  @Test
  void checkIfEmailExistsShouldBeFalse() {
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            "za@gmail.com",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    assertThat(accountRepo.checkIfUsernameExists("notInDatabase@gmail.com")).isFalse();
  }

  @Test
  void enableAccount() {
    Account account =
        new Account(
            "Zach",
            "Warunek",
            "za@gmail.com",
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    accountRepo.enableAccount(account.getUsername());

    Optional<Account> accountOptional = accountRepo.findById(account.getId());
    assertThat(accountOptional.isPresent()).isTrue();
    assertThat(accountOptional.get().isEnabled()).isTrue();
  }
}
