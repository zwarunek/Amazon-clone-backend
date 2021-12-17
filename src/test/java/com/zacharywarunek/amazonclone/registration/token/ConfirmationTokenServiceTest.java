package com.zacharywarunek.amazonclone.registration.token;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepo confirmationTokenRepo;
    private ConfirmationTokenService confirmationTokenService;

    @BeforeEach
    void setUp() {
        confirmationTokenService = new ConfirmationTokenService(confirmationTokenRepo);
    }

    @Test
    void saveConfirmationToken() {

        String token = UUID.randomUUID().toString();
        Account account = new Account();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        ArgumentCaptor<ConfirmationToken> accountArgumentCaptor = ArgumentCaptor.forClass(ConfirmationToken.class);

        verify(confirmationTokenRepo).save(accountArgumentCaptor.capture());

        ConfirmationToken capturedToken = accountArgumentCaptor.getValue();
        assertEquals(capturedToken, confirmationToken);
    }

    @Test
    void getToken() {

        String token = UUID.randomUUID().toString();
        confirmationTokenService.getToken(token);

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);

        verify(confirmationTokenRepo).findByToken(tokenCaptor.capture());

        String capturedToken = tokenCaptor.getValue();
        assertEquals(capturedToken, token);
    }

    @Test
    void setConfirmedAt() {

        String token = UUID.randomUUID().toString();
        LocalDateTime issued = LocalDateTime.now();
        LocalDateTime expires = LocalDateTime.now().plusMinutes(15);
        confirmationTokenService.setConfirmedAt(token);

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(confirmationTokenRepo).updateConfirmedAt(tokenCaptor.capture(), timeCaptor.capture());

        String capturedToken = tokenCaptor.getValue();
        LocalDateTime capturedTime = timeCaptor.getValue();
        assertEquals(capturedToken, token);
        assertTrue(capturedTime.isBefore(expires));
    }

    @Test
    void deleteAllAtAccountId() {

        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
        account.setId(5);
        confirmationTokenService.deleteAllAtAccountId(account);

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        verify(confirmationTokenRepo).deleteAllByAccountId(accountArgumentCaptor.capture());

        Account capturedAccount = accountArgumentCaptor.getValue();
        assertEquals(capturedAccount, account);
    }
}
