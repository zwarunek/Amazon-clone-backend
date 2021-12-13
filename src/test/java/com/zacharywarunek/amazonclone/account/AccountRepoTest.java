package com.zacharywarunek.amazonclone.account;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AccountRepoTest {

    @Autowired
    private AccountRepo accountRepo;

    @AfterEach
    void tearDown() {
        accountRepo.deleteAll();
    }

    @Test
    void findAccountByEmailShouldBeTrue() {
        String email = "Zach@gmail.com";
        Account account = new Account(
                "Zach",
                "Warunek",
                "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
                email,
                AccountRole.USER
        );
        accountRepo.save(account);
        Optional<Account> accountOptional = accountRepo.findAccountByEmail(email);
        assertTrue(accountOptional.isPresent());
        assertEquals(account, accountOptional.get());
    }

    @Test
    void findAccountByEmailShouldBeFalse() {
        Account account = new Account(
                "Zach",
                "Warunek",
                "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
                "Zach@gmail.com",
                AccountRole.USER
        );
        accountRepo.save(account);
        Optional<Account> accountOptional = accountRepo.findAccountByEmail("notInDatabase@gmail.com");
        assertFalse(accountOptional.isPresent());
    }

    @Test
    void checkIfEmailExistsShouldBeTrue() {
        String email = "za@gmail.com";
        Account account = new Account(
                "Zach",
                "Warunek",
                "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
                email,
                AccountRole.USER
        );
        accountRepo.save(account);

        assertTrue(accountRepo.checkIfEmailExists(email));
    }

    @Test
    void checkIfEmailExistsShouldBeFalse() {
        Account account = new Account(
                "Zach",
                "Warunek",
                "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
                "za@gmail.com",
                AccountRole.USER
        );
        accountRepo.save(account);

        assertFalse(accountRepo.checkIfEmailExists("notInDatabase@gmail.com"));
    }
}