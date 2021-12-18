package com.zacharywarunek.amazonclone.registration.token;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ConfirmationTokenRepoTest {

    @Autowired
    private ConfirmationTokenRepo confirmationTokenRepo;
    @Autowired
    private AccountRepo accountRepo;

    @AfterEach
    void tearDown() {
        confirmationTokenRepo.deleteAll();
        accountRepo.deleteAll();
    }

    @Test
    void findByToken() {
        Account account = new Account("Zach",
                                      "Warunek",
                                      "Zach@gmail.com",
                                      "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
                                      AccountRole.ROLE_USER);
        accountRepo.save(account);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
        confirmationTokenRepo.save(confirmationToken);
        Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepo.findByToken(token);
        assertTrue(confirmationTokenOptional.isPresent());
        assertEquals(confirmationToken, confirmationTokenOptional.get());
    }

    @Test
    void findByTokenNotFound() {
        Account account = new Account("Zach",
                                      "Warunek",
                                      "Zach@gmail.com",
                                      "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
                                      AccountRole.ROLE_USER);
        accountRepo.save(account);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
        confirmationTokenRepo.save(confirmationToken);
        Optional<ConfirmationToken> confirmationTokenOptional =
                confirmationTokenRepo.findByToken(UUID.randomUUID().toString());
        assertFalse(confirmationTokenOptional.isPresent());
    }

    @Test
    void updateConfirmedAt() {
        Account account = new Account("Zach",
                                      "Warunek",
                                      "Zach@gmail.com",
                                      "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
                                      AccountRole.ROLE_USER);
        accountRepo.save(account);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
        confirmationTokenRepo.save(confirmationToken);
        LocalDateTime now = LocalDateTime.now();
        confirmationTokenRepo.updateConfirmedAt(token, now);

        Optional<ConfirmationToken> tokenOptional = confirmationTokenRepo.findById(confirmationToken.getId());

        assertTrue(tokenOptional.isPresent());
        assertEquals(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                     tokenOptional.get().getConfirmed_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    }

    @Test
    void deleteAllByAccountId() {
        Account account = new Account("Zach",
                                      "Warunek",
                                      "Zach@gmail.com",
                                      "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
                                      AccountRole.ROLE_USER);
        accountRepo.save(account);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
        confirmationTokenRepo.save(confirmationToken);
        confirmationTokenRepo.deleteAllByAccountId(account);
        List<ConfirmationToken> tokens = confirmationTokenRepo.findAll();
        assertTrue(tokens.isEmpty());
        assertFalse(confirmationTokenRepo.findByToken(token).isPresent());
    }

}
