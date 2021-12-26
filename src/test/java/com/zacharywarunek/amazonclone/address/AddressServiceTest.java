package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
  @Mock private AddressRepo addressRepo;
  @Mock private AccountRepo accountRepo;
  @InjectMocks private AddressService addressService;
  private Account account;
  private Address addressDetails;
  private Address address;

  @BeforeEach
  void setupAccount() {
    account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    account.setId(1L);
    addressDetails =
        new Address(
            account,
            "changedAddress",
            "changedCity",
            "CA",
            54321,
            false,
            "changedFirst",
            "changedLast");
    address =
        new Address(account, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");
  }

  @Test
  void getAllAddresses() throws EntityNotFoundException {
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(addressRepo.findAddressByAccount(account)).willReturn(Collections.singletonList(address));
    List<Address> actualAddresses = addressService.getAllAddresses(account.getId());
    assertThat(actualAddresses).isEqualTo(Collections.singletonList(address));
  }

  @Test
  void getAllAddressesAccountNotFound() {
    given(accountRepo.findById(1L)).willReturn(Optional.empty());
    assertThatThrownBy(() -> addressService.getAllAddresses(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_ID_NOT_FOUND.label, account.getId()));
    verify(addressRepo, never()).findAddressByAccount(any());
  }

  @Test
  void createAddressTest() throws BadRequestException, EntityNotFoundException {
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(addressRepo.save(any())).willReturn(address);
    Address actualAddress = addressService.createAddress(account.getId(), address);
    address.setAccount(account);
    assertThat(actualAddress).isEqualTo(address);
  }

  @Test
  void createAddressNullValues() {
    address.setAddress(null);
    assertThatThrownBy(() -> addressService.createAddress(account.getId(), address))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(ExceptionResponses.NULL_VALUES.label);
    verify(accountRepo, never()).findById(any());
    verify(addressRepo, never()).findAddressByAccount(any());
  }

  @Test
  void createAddressAccountNotFound() {
    given(accountRepo.findById(1L)).willReturn(Optional.empty());
    assertThatThrownBy(() -> addressService.createAddress(account.getId(), address))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_ID_NOT_FOUND.label, account.getId()));
    verify(addressRepo, never()).findAddressByAccount(any());
  }

  @Test
  void updateAddressTest() throws EntityNotFoundException, UnauthorizedException {
    address.setId(1L);
    address.setAccount(account);
    given(accountRepo.findById(account.getId())).willReturn(Optional.of(account));
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    Address actualAddress =
        addressService.updateAddress(account.getId(), address.getId(), addressDetails);
    assertThat(actualAddress).isEqualTo(address);
  }

  @Test
  void updateAddressAddressNotFound() {
    address.setId(1L);
    address.setAccount(account);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(addressRepo.findById(1L)).willReturn(Optional.empty());
    assertThatThrownBy(
            () -> addressService.updateAddress(account.getId(), address.getId(), addressDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ADDRESS_NOT_FOUND.label, address.getId()));
  }

  @Test
  void updateAddressAccountNotFound() {
    address.setId(1L);
    address.setAccount(account);
    given(accountRepo.findById(1L)).willReturn(Optional.empty());
    assertThatThrownBy(
            () -> addressService.updateAddress(account.getId(), address.getId(), addressDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_ID_NOT_FOUND.label, account.getId()));
    verify(addressRepo, never()).findById(any());
  }

  @Test
  void updateAddressUnauthorized() {
    address.setId(1L);
    address.setAccount(account);
    Account newAccount = new Account();
    newAccount.setId(2L);
    given(accountRepo.findById(newAccount.getId())).willReturn(Optional.of(newAccount));
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    assertThatThrownBy(
            () -> addressService.updateAddress(newAccount.getId(), address.getId(), addressDetails))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.ADDRESS_UNAUTHORIZED.label,
                address.getId(),
                newAccount.getId()));
  }

  @Test
  void getFavorite() throws EntityNotFoundException {
    address.setId(1L);
    address.setAccount(account);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(addressRepo.findFavoriteAddressByAccount(account)).willReturn(Optional.of(address));
    Address actualAddress = addressService.getFavorite(account.getId());
    assertThat(actualAddress).isEqualTo(address);
  }

  @Test
  void getFavoriteAccountNotFound() {
    address.setId(1L);
    address.setAccount(account);
    given(accountRepo.findById(1L)).willReturn(Optional.empty());
    assertThatThrownBy(() -> addressService.getFavorite(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_ID_NOT_FOUND.label, account.getId()));
  }

  @Test
  void getFavoriteAddressNotFound() {
    address.setId(1L);
    address.setAccount(account);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(addressRepo.findFavoriteAddressByAccount(account)).willReturn(Optional.empty());
    assertThatThrownBy(() -> addressService.getFavorite(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.NO_FAVORITE_ADDRESS.label, address.getId()));
  }

  @Test
  void setFavorite() throws EntityNotFoundException, UnauthorizedException {
    address.setId(1L);
    address.setAccount(account);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    addressService.setFavorite(account.getId(), address.getId());
  }

  @Test
  void setFavoriteAddressNotFound() {
    address.setId(1L);
    address.setAccount(account);
    given(addressRepo.findById(address.getId())).willReturn(Optional.empty());
    assertThatThrownBy(() -> addressService.setFavorite(account.getId(), address.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ADDRESS_NOT_FOUND.label, account.getId()));
  }

  @Test
  void setFavoriteUnauthorized() {
    address.setId(1L);
    address.setAccount(account);
    Account newAccount = new Account();
    newAccount.setId(2L);
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    assertThatThrownBy(() -> addressService.setFavorite(newAccount.getId(), address.getId()))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.ADDRESS_UNAUTHORIZED.label,
                address.getId(),
                newAccount.getId()));
  }

  @Test
  void setFavoriteAccountNotFound() {
    address.setId(1L);
    address.setAccount(account);
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    given(accountRepo.findById(1L)).willReturn(Optional.empty());
    assertThatThrownBy(() -> addressService.setFavorite(account.getId(), address.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_ID_NOT_FOUND.label, account.getId()));
  }

  @Test
  void deleteById() throws EntityNotFoundException {
    address.setId(1L);
    address.setAccount(account);
    given(addressRepo.findById(any())).willReturn(Optional.of(address));
    addressService.deleteAddress(address.getId());
    verify(addressRepo).delete(address);
  }
}
