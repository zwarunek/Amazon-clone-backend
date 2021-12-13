package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.exeption.BadRequestException;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepo accountRepo;
    @Mock private JwtUtil jwtUtil;
    @Mock private ConfirmationTokenService confirmationTokenService;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepo, jwtUtil, confirmationTokenService);
    }

    @Test
    void getAllAccounts() {
        accountService.getAllAccounts();

        verify(accountRepo).findAll();
    }

    @Test
    void shouldRegister() {
        Account account = new Account(
                "Zach",
                "Warunek",
                "password1234",
                "Zach@gmail.com",
                AccountRole.USER
        );
        accountService.register(account);

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepo).save(accountArgumentCaptor.capture());

        Account capturedAccount = accountArgumentCaptor.getValue();
        assertEquals(capturedAccount, account);
    }

    @Test
    void registerEmailTaken() {
        Account account = new Account(
                "Zach",
                "Warunek",
                "password1234",
                "Zach@gmail.com",
                AccountRole.USER
        );

        given(accountRepo.checkIfEmailExists(anyString()))
                .willReturn(true);

        Throwable exception = assertThrows(BadRequestException.class, () -> accountService.register(account));
        assertEquals("An account with that email " + account.getUsername() + " already exists", exception.getMessage());
        verify(accountRepo, never()).save(any());
    }

    @Test
    void registerNullValues() {
        Account account = new Account(
                null,
                "Warunek",
                "password1234",
                "Zach@gmail.com",
                AccountRole.USER
        );

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> accountService.register(account));
        assertEquals("An error occurred when creating the account: Null values present", exception.getMessage());
        verify(accountRepo, never()).save(any());
    }

    @Test
    void authenticate() {
    }

    @Test
    void updateAccount() {
    }

    @Test
    void deleteAccount() {
    }
}
