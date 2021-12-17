package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import com.zacharywarunek.amazonclone.util.AuthRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepo accountRepo;
    @InjectMocks
    private JwtUtil jwtUtil;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepo, jwtUtil, passwordEncoder, confirmationTokenService);
    }

    @Test
    void getAllAccounts() {
        accountService.getAllAccounts();

        verify(accountRepo).findAll();
    }

    @Test
    void shouldRegister() {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
        accountService.register(account);

        ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepo).save(accountArgumentCaptor.capture());

        Account capturedAccount = accountArgumentCaptor.getValue();
        assertEquals(capturedAccount, account);
    }

    @Test
    void registerEmailTaken() {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);

        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.register(account));
        assertEquals("An account with that email " + account.getUsername() + " already exists", exception.getReason());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(accountRepo, never()).save(any());
    }

    @Test
    void registerNullValues() {
        Account account = new Account(null, "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.register(account));
        assertEquals("An error occurred when creating the account: Null values present", exception.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(accountRepo, never()).save(any());
    }

    @Test
    void shouldAuthenticate() {
        String password = "password1234";
        Account account =
                new Account("Zach", "Warunek", "Zach@gmail.com", passwordEncoder.encode(password), AccountRole.ROLE_USER);
        AuthRequest authRequest = new AuthRequest("Zach@gmail.com", password);
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.of(account));

        ResponseEntity<Object> response = accountService.authenticate(authRequest);
        assertEquals(authRequest.getUsername(),
                jwtUtil.getUsernameFromToken(response.getHeaders().get("Authorization").get(0)));
    }

    @Test
    void authenticateUsernameNotFound() {
        AuthRequest authRequest = new AuthRequest("NotInDB@gmail.com", "password1234");
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.authenticate(authRequest));
        assertEquals("Username or Password was incorrect", exception.getReason());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void authenticateFieldsNotFilled() {
        AuthRequest authRequest = new AuthRequest(null, "password");
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.authenticate(authRequest));
        assertEquals("'username' or 'password' fields not found", exception.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(accountRepo, never()).findAccountByUsername(any());
    }

    @Test
    void updateAccount() {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "passwor", AccountRole.ROLE_USER);
        Account accountDetails = new Account("ZachChange", null, "Zach@gmail.comChange", "password1234", null);
        given(accountRepo.findById(1)).willReturn(java.util.Optional.of(account));
        accountService.updateAccount(1, accountDetails);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepo).save(accountCaptor.capture());

        assertEquals(accountCaptor.getValue().getFirst_name(), accountDetails.getFirst_name());
        assertNotEquals(accountCaptor.getValue().getLast_name(), accountDetails.getLast_name());
        assertEquals(accountCaptor.getValue().getUsername(), accountDetails.getUsername());
        assertEquals(accountCaptor.getValue().getPassword(), accountDetails.getPassword());
        assertNotEquals(accountCaptor.getValue().getRole(), accountDetails.getRole());
    }

    @Test
    void updateAccountUsernameAlreadyExists() {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
        Account accountDetails = new Account("ZachChange", null, "Zach@gmail.comChange", "password1234", null);
        given(accountRepo.findById(1)).willReturn(java.util.Optional.of(account));
        given(accountRepo.findAccountByUsername(accountDetails.getUsername())).willReturn(
                java.util.Optional.of(account));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.updateAccount(1, accountDetails));
        assertEquals("Username is already in use", exception.getReason());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(accountRepo, never()).save(any());
    }

    @Test
    void updateAccountDoesntExist() {
        Account accountDetails = new Account("ZachChange", "WarunekChange", "Zach@gmail.comChange", null, null);
        given(accountRepo.findById(1)).willReturn(java.util.Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.updateAccount(1, accountDetails));
        assertEquals("Account with id " + 1 + " doesn't exist", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(accountRepo, never()).save(any());
    }

    @Test
    void shouldDeleteAccount() {
        String password = "password1234";
        Account account =
                new Account("Zach", "Warunek", "Zach@gmail.com", passwordEncoder.encode(password), AccountRole.ROLE_USER);
        account.setId(1);
        given(accountRepo.findById(account.getId())).willReturn(java.util.Optional.of(account));

        accountService.deleteAccount(account.getId());

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        ArgumentCaptor<Integer> accountIdCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(confirmationTokenService).deleteAllAtAccountId(accountCaptor.capture());
        verify(accountRepo).deleteById(accountIdCaptor.capture());

        assertEquals(account, accountCaptor.getValue());
        assertEquals(account.getId(), accountIdCaptor.getValue());
    }

    @Test
    void deleteAccountNotFound() {
        given(accountRepo.findById(1)).willReturn(java.util.Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.deleteAccount(1));
        assertEquals("Account with id " + 1 + " doesn't exist", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(confirmationTokenService, never()).deleteAllAtAccountId(any());
        verify(accountRepo, never()).deleteById(any());
    }

    @Test
    void shouldEnableAccount() {
        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);

        accountService.enableAccount("ANYSTRING");

        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(accountRepo).enableAccount(usernameCaptor.capture());

        assertEquals("ANYSTRING", usernameCaptor.getValue());


    }

    @Test
    void enableAccountNotFound() {
        given(accountRepo.checkIfUsernameExists(anyString())).willReturn(false);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.enableAccount("ANYSTRING"));
        assertEquals("Account with username ANYSTRING doesn't exist", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(accountRepo, never()).deleteById(any());

    }

    @Test
    void shouldLoadUserByUsername() {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", passwordEncoder.encode("password1234"),
                AccountRole.ROLE_USER);
        given(accountRepo.findAccountByUsername(account.getUsername())).willReturn(java.util.Optional.of(account));

        UserDetails userDetails = accountService.loadUserByUsername(account.getUsername());
        assertEquals(account.getUsername(), userDetails.getUsername());
        assertEquals(account.getPassword(), userDetails.getPassword());
        assertEquals(account.getAuthorities().toString(), userDetails.getAuthorities().toString());

    }

    @Test
    void loadUserByUsernameNotFound() {
        given(accountRepo.findAccountByUsername(anyString())).willReturn(java.util.Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> accountService.loadUserByUsername("ANYSTRING"));
        assertEquals("Account with username ANYSTRING doesn't exist", exception.getReason());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

    }
}
