package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.address.AddressRepo;
import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.exceptions.*;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationToken;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import com.zacharywarunek.amazonclone.util.AuthRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock PasswordEncoder passwordEncoder;
  @Mock private AccountRepo accountRepo;
  @Mock private AddressRepo addressRepo;
  @InjectMocks private JwtUtil testJwtUtil;
  @Mock private JwtUtil jwtUtil;
  @Mock private ConfirmationTokenService confirmationTokenService;
  @InjectMocks private BCryptPasswordEncoder testPasswordEncoder;
  @InjectMocks private AccountService accountService;
  private Account account;
  private AuthRequest authRequest;
  private String password;
  private AccountDetails accountDetails;

  @BeforeEach
  void setupAccount() {
    password = "password1234";
    account = new Account("Zach", "Warunek", "Zach@gmail.com", password, AccountRole.ROLE_USER);
    accountDetails =
        new AccountDetails("FNChanged", "LNchanged", "Zachdfsdsa@gmail.com", "something");
    authRequest = new AuthRequest("Zach@gmail.com", password);
  }

  @Test
  void getAllAccounts() {
    accountService.getAll();

    verify(accountRepo).findAll();
  }

  @Test
  void shouldRegister() throws BadRequestException, UsernameTakenException {
    given(passwordEncoder.encode(password)).willReturn(testPasswordEncoder.encode(password));
    String token = accountService.create(account);
    ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
    ArgumentCaptor<ConfirmationToken> confirmationTokenArgumentCaptor =
        ArgumentCaptor.forClass(ConfirmationToken.class);

    verify(accountRepo).save(accountArgumentCaptor.capture());
    verify(confirmationTokenService)
        .saveConfirmationToken(confirmationTokenArgumentCaptor.capture());

    Account capturedAccount = accountArgumentCaptor.getValue();
    assertThat(account)
        .usingRecursiveComparison()
        .ignoringFields("password")
        .isEqualTo(capturedAccount);
    assertThat(testPasswordEncoder.matches(password, capturedAccount.getPassword())).isTrue();
    assertThat(confirmationTokenArgumentCaptor.getValue().getToken()).isEqualTo(token);
  }

  @Test
  void registerEmailTaken() {
    given(accountRepo.checkIfUsernameExists(anyString())).willReturn(true);

    assertThatThrownBy(() -> accountService.create(account))
        .isInstanceOf(UsernameTakenException.class)
        .hasMessage(String.format(ExceptionResponses.USERNAME_TAKEN.label, account.getUsername()));
    verify(accountRepo, never()).save(any());
  }

  @Test
  void registerNullValues() {
    account.setPassword(null);
    assertThatThrownBy(() -> accountService.create(account))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(ExceptionResponses.NULL_VALUES.label);
    verify(accountRepo, never()).save(any());
  }

  @Test
  void shouldAuthenticate() throws UnauthorizedException, BadRequestException {
    account.setPassword(testPasswordEncoder.encode(password));
    given(accountRepo.findAccountByUsername(authRequest.getUsername()))
        .willReturn(java.util.Optional.of(account));
    given(passwordEncoder.matches(authRequest.getPassword(), account.getPassword()))
        .willReturn(testPasswordEncoder.matches(authRequest.getPassword(), account.getPassword()));
    given(jwtUtil.generateToken(account)).willReturn(testJwtUtil.generateToken(account));
    assertThat(testJwtUtil.getUsernameFromToken(accountService.authenticate(authRequest)))
        .isEqualTo(authRequest.getUsername());
  }

  @Test
  void authenticateThrowBadRequest() {
    authRequest.setPassword(null);
    assertThatThrownBy(() -> accountService.authenticate(authRequest))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(NULL_VALUES.label);
    verify(accountRepo, never()).findAccountByUsername(any());
    verify(jwtUtil, never()).generateToken(any());
  }

  @Test
  void authenticateThrowUnauthorizedRequest() {
    given(accountRepo.findAccountByUsername(anyString())).willReturn(Optional.empty());
    assertThatThrownBy(() -> accountService.authenticate(authRequest))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Unauthorized");
    verify(jwtUtil, never()).generateToken(any());
  }

  @Test
  void authenticateAuthRequestWrong() {
    given(accountRepo.findAccountByUsername(anyString())).willReturn(Optional.empty());
    assertThatThrownBy(() -> accountService.authenticate(authRequest))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage("Unauthorized");
    verify(jwtUtil, never()).generateToken(any());
  }

  @Test
  void updateAccount() throws EntityNotFoundException, UsernameTakenException {
    given(passwordEncoder.encode(accountDetails.getPassword()))
        .willReturn(testPasswordEncoder.encode(accountDetails.getPassword()));
    given(accountRepo.findById(1L)).willReturn(java.util.Optional.of(account));
    Account updatedAccount = accountService.update(1L, accountDetails);
    assertThat(
            testPasswordEncoder.matches(accountDetails.getPassword(), updatedAccount.getPassword()))
        .isTrue();
    assertThat(account).usingRecursiveComparison().ignoringFields("password").isEqualTo(account);
  }

  @Test
  void updateAccountThrowsNotFound() {
    given(accountRepo.findById(any())).willReturn(java.util.Optional.empty());
    assertThatThrownBy(() -> accountService.update(1L, accountDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ACCOUNT_ID_NOT_FOUND.label, 1L));
  }

  @Test
  void updateAccountThrowsUsernameTaken() {
    given(accountRepo.findById(1L)).willReturn(java.util.Optional.of(account));
    given(accountRepo.findAccountByUsername(accountDetails.getUsername()))
        .willReturn(java.util.Optional.of(account));
    assertThatThrownBy(() -> accountService.update(1L, accountDetails))
        .isInstanceOf(UsernameTakenException.class)
        .hasMessage(String.format(USERNAME_TAKEN.label, accountDetails.getUsername()));
  }

  @Test
  void shouldDeleteAccount() throws EntityNotFoundException {
    account.setId(1L);
    given(accountRepo.findById(account.getId())).willReturn(java.util.Optional.of(account));

    accountService.delete(account.getId());

    ArgumentCaptor<Account> accountCaptor1 = ArgumentCaptor.forClass(Account.class);
    ArgumentCaptor<Account> accountCaptor2 = ArgumentCaptor.forClass(Account.class);
    ArgumentCaptor<Long> accountIdCaptor = ArgumentCaptor.forClass(Long.class);
    verify(confirmationTokenService).deleteAllAtAccount(accountCaptor1.capture());
    verify(addressRepo).deleteAllAtAccount(accountCaptor2.capture());
    verify(accountRepo).deleteById(accountIdCaptor.capture());

    assertThat(accountCaptor1.getValue()).isEqualTo(account);
    assertThat(accountCaptor2.getValue()).isEqualTo(account);
    assertThat(accountIdCaptor.getValue()).isEqualTo(account.getId());
  }

  @Test
  void deleteAccountNotFound() {
    account.setId(1L);
    given(accountRepo.findById(account.getId())).willReturn(java.util.Optional.empty());
    assertThatThrownBy(() -> accountService.delete(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ACCOUNT_ID_NOT_FOUND.label, account.getId()));
    verify(confirmationTokenService, never()).deleteAllAtAccount(any());
    verify(addressRepo, never()).deleteAllAtAccount(any());
    verify(accountRepo, never()).deleteById(any());
  }

  @Test
  void shouldLoadUserByUsername() {
    given(accountRepo.findAccountByUsername(account.getUsername()))
        .willReturn(java.util.Optional.of(account));
    UserDetails userDetails = accountService.loadUserByUsername(account.getUsername());
    assertThat(userDetails.getUsername()).isEqualTo(account.getUsername());
    assertThat(userDetails.getPassword()).isEqualTo(account.getPassword());
    assertThat(userDetails.getAuthorities().toString())
        .isEqualTo(account.getAuthorities().toString());
  }

  @Test
  void loadUserByUsernameNotFound() {
    given(accountRepo.findAccountByUsername(account.getUsername()))
        .willReturn(java.util.Optional.empty());
    assertThatThrownBy(() -> accountService.loadUserByUsername(account.getUsername()))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage(String.format(USERNAME_NOT_FOUND.name(), account.getUsername()));
  }

  @Test
  void shouldEnableAccount() {
    accountService.enable(account.getUsername());
    ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
    verify(accountRepo).enableAccount(usernameCaptor.capture());
    assertThat(usernameCaptor.getValue()).isEqualTo(account.getUsername());
  }
}
