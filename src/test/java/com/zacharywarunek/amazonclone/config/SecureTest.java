package com.zacharywarunek.amazonclone.config;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.address.Address;
import com.zacharywarunek.amazonclone.address.AddressRepo;
import com.zacharywarunek.amazonclone.address.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SecureTest {
  @Mock private AddressRepo addressRepo;
  @Mock private AccountRepo accountRepo;
  @InjectMocks private Secure secure;
  private Authentication authAdmin;
  private Authentication authUser;
  private Account accountAdmin;
  private Account accountUser;
  private Address addressAdmin;
  private Address addressUser;

  @BeforeEach
  void setupAccount() {
    accountAdmin =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    accountAdmin.setId(1L);
    authAdmin =
        new UsernamePasswordAuthenticationToken(
                accountAdmin, accountAdmin.getPassword(), accountAdmin.getAuthorities());
    accountUser =
            new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    accountUser.setId(2L);
    authUser =
            new UsernamePasswordAuthenticationToken(
                    accountUser, accountUser.getPassword(), accountUser.getAuthorities());
    addressUser =
            new Address(
                    accountUser,
                    "changedAddress",
                    "changedCity",
                    "CA",
                    54321,
                    false,
                    "changedFirst",
                    "changedLast");
    addressAdmin =
            new Address(
                    accountAdmin,
                    "changedAddress",
                    "changedCity",
                    "CA",
                    54321,
                    false,
                    "changedFirst",
                    "changedLast");
  }

  @Test
  void checkAccountIdUser() {
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.of(accountUser));
    assertThat(secure.checkAccountIdAuth(authUser, accountUser.getId())).isTrue();
  }

  @Test
  void checkAccountIdUserAccountNotFound() {
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.empty());
    assertThat(secure.checkAccountIdAuth(authUser, accountUser.getId())).isFalse();
  }

  @Test
  void checkAccountIdUserWrongId() {
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.of(accountUser));
    assertThat(secure.checkAccountIdAuth(authUser, accountAdmin.getId())).isFalse();
  }

  @Test
  void checkAccountIdAdmin() {
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.of(accountAdmin));
    assertThat(secure.checkAccountIdAuth(authAdmin, accountAdmin.getId())).isTrue();
  }

  @Test
  void checkAccountIdAdminAccountNotFound() {
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.empty());
    assertThat(secure.checkAccountIdAuth(authAdmin, accountAdmin.getId())).isTrue();
  }

  @Test
  void checkAddressIdUser() {
    given(addressRepo.findById(any())).willReturn(Optional.of(addressUser));
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.of(accountUser));
    assertThat(secure.checkAddressIdAuth(authUser, accountUser.getId(), addressUser.getId())).isTrue();
  }

  @Test
  void checkAddressIdUserAddressNotFound() {
    given(addressRepo.findById(any())).willReturn(Optional.empty());
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.of(accountUser));
    assertThat(secure.checkAddressIdAuth(authUser, accountUser.getId(), addressUser.getId())).isFalse();
  }

  @Test
  void checkAddressIdUserAccountNotFound() {
    given(addressRepo.findById(any())).willReturn(Optional.of(addressUser));
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.empty());
    assertThat(secure.checkAddressIdAuth(authUser, accountUser.getId(), addressUser.getId())).isFalse();
  }

  @Test
  void checkAddressIdUserWrongAddressAccount() {
    given(addressRepo.findById(any())).willReturn(Optional.of(addressAdmin));
    given(accountRepo.findAccountByUsername(any())).willReturn(Optional.of(accountUser));
    assertThat(secure.checkAddressIdAuth(authUser, accountAdmin.getId(), addressUser.getId())).isFalse();
  }
}
