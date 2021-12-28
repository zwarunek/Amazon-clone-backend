package com.zacharywarunek.amazonclone.address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
  @Mock private AddressRepo addressRepo;
  @Mock private AccountService accountService;
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
    address.setId(1L);
  }

  @Test
  void getAllAddresses() throws EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressRepo.findByAccount(account)).willReturn(Collections.singletonList(address));
    List<Address> actualAddresses = addressService.getAll(account.getId());
    assertThat(actualAddresses).isEqualTo(Collections.singletonList(address));
  }

  @Test
  void getAllAddressesAccountNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(() -> addressService.getAll(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
    verify(addressRepo, never()).findByAccount(any());
  }

  @Test
  void createAddressTest() throws BadRequestException, EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressRepo.save(any())).willReturn(address);
    Address actualAddress = addressService.create(account.getId(), address);
    address.setAccount(account);
    assertThat(actualAddress).isEqualTo(address);
  }

  @Test
  void createAddressNullValues() throws EntityNotFoundException {
    address.setAddress(null);
    assertThatThrownBy(() -> addressService.create(account.getId(), address))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(ExceptionResponses.NULL_VALUES.label);
    verify(accountService, never()).findById(any());
    verify(addressRepo, never()).findByAccount(any());
  }

  @Test
  void createAddressAccountNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(() -> addressService.create(account.getId(), address))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
    verify(addressRepo, never()).findByAccount(any());
  }

  @Test
  void updateAddressTest() throws EntityNotFoundException, UnauthorizedException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    Address actualAddress = addressService.update(account.getId(), address.getId(), addressDetails);
    assertThat(actualAddress).isEqualTo(address);
  }

  @Test
  void updateAddressAddressNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressRepo.findById(1L)).willReturn(Optional.empty());
    assertThatThrownBy(
            () -> addressService.update(account.getId(), address.getId(), addressDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ADDRESS_NOT_FOUND.label, address.getId()));
  }

  @Test
  void updateAddressAccountNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(
            () -> addressService.update(account.getId(), address.getId(), addressDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
    verify(addressRepo, never()).findById(any());
  }

  @Test
  void updateAddressUnauthorized() throws EntityNotFoundException {
    Account newAccount = new Account();
    newAccount.setId(2L);
    given(accountService.findById(2L)).willReturn(newAccount);
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    assertThatThrownBy(
            () -> addressService.update(newAccount.getId(), address.getId(), addressDetails))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.ADDRESS_UNAUTHORIZED.label,
                address.getId(),
                newAccount.getId()));
  }

  @Test
  void getFavorite() throws EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressRepo.findFavoriteAddressByAccount(account)).willReturn(Optional.of(address));
    Address actualAddress = addressService.getFavorite(account.getId());
    assertThat(actualAddress).isEqualTo(address);
  }

  @Test
  void getFavoriteAccountNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(() -> addressService.getFavorite(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
  }

  @Test
  void getFavoriteAddressNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressRepo.findFavoriteAddressByAccount(account)).willReturn(Optional.empty());
    assertThatThrownBy(() -> addressService.getFavorite(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.NO_FAVORITE_ADDRESS.label, address.getId()));
  }

  @Test
  void setFavorite() throws EntityNotFoundException, UnauthorizedException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    addressService.setFavorite(account.getId(), address.getId());
  }

  @Test
  void setFavoriteAddressNotFound() {
    given(addressRepo.findById(address.getId())).willReturn(Optional.empty());
    assertThatThrownBy(() -> addressService.setFavorite(account.getId(), address.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ADDRESS_NOT_FOUND.label, account.getId()));
  }

  @Test
  void setFavoriteUnauthorized() {
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    assertThatThrownBy(() -> addressService.setFavorite(2L, address.getId()))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.ADDRESS_UNAUTHORIZED.label,
                address.getId(),
                2L));
  }

  @Test
  void setFavoriteAccountNotFound() throws EntityNotFoundException {
    given(addressRepo.findById(address.getId())).willReturn(Optional.of(address));
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(() -> addressService.setFavorite(account.getId(), address.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
  }

  @Test
  void deleteById() throws EntityNotFoundException, UnauthorizedException {
    given(addressRepo.findById(any())).willReturn(Optional.of(address));
    addressService.delete(account.getId(), address.getId());
    verify(addressRepo).delete(address);
  }

  @Test
  void deleteByIdUnauthorized() {
    given(addressRepo.findById(any())).willReturn(Optional.of(address));
    assertThatThrownBy(() -> addressService.delete(2L, address.getId()))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.ADDRESS_UNAUTHORIZED.label,
                address.getId(),
                2L));
  }
}
