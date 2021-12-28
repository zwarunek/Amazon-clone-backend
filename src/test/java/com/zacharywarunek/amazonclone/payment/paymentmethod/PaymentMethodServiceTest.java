package com.zacharywarunek.amazonclone.payment.paymentmethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.address.Address;
import com.zacharywarunek.amazonclone.address.AddressService;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentType;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentTypeService;
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
class PaymentMethodServiceTest {
  @InjectMocks private PaymentMethodService paymentMethodService;
  @Mock private PaymentMethodRepo paymentMethodRepo;
  @Mock private PaymentTypeService paymentTypeService;
  @Mock private AccountService accountService;
  @Mock private AddressService addressService;
  private Account account;
  private Address address;
  private PaymentType paymentType;
  private PaymentMethod paymentMethod;
  private PaymentMethodDetails paymentMethodDetails;

  @BeforeEach
  void setupAccount() {
    account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    address =
        new Address(account, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");
    paymentType = new PaymentType("NAME", "SRC");
    paymentMethod =
        new PaymentMethod(
            account, paymentType, "NAME", "1111222233334444", "12/12", "123", false, address);
    paymentMethodDetails =
        new PaymentMethodDetails(1L, "NAME", "1111222233334444", "12/12", "123", 1L);
    account.setId(1L);
    address.setId(1L);
    paymentType.setId(1L);
    paymentMethod.setId(1L);
  }

  @Test
  void getAllAddresses() throws EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(paymentMethodRepo.findByAccount(account))
        .willReturn(Collections.singletonList(paymentMethod));
    List<PaymentMethod> actualPaymentMethods =
        paymentMethodService.getAll(account.getId());
    assertThat(actualPaymentMethods).isEqualTo(Collections.singletonList(paymentMethod));
  }

  @Test
  void getAllAddressesAccountNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(() -> paymentMethodService.getAll(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
    verify(paymentMethodRepo, never()).findByAccount(any());
  }

  @Test
  void createAddressTest() throws BadRequestException, EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressService.findById(1L)).willReturn(address);
    given(paymentTypeService.findById(1L)).willReturn(paymentType);
    given(paymentMethodRepo.save(any())).willReturn(paymentMethod);
    PaymentMethod actualPaymentMethod =
        paymentMethodService.create(account.getId(), paymentMethodDetails);
    paymentMethod.setAccount(account);
    assertThat(actualPaymentMethod).isEqualTo(paymentMethod);
  }

  @Test
  void createAddressNullValues() throws EntityNotFoundException {
    paymentMethodDetails.setPaymentTypeId(null);
    assertThatThrownBy(
            () -> paymentMethodService.create(account.getId(), paymentMethodDetails))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(ExceptionResponses.NULL_VALUES.label);
    verify(accountService, never()).findById(any());
    verify(paymentMethodRepo, never()).findByAccount(any());
  }

  @Test
  void createAddressAccountNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(
            () -> paymentMethodService.create(account.getId(), paymentMethodDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
    verify(paymentMethodRepo, never()).findByAccount(any());
  }

  @Test
  void updatePaymentMethodTest() throws EntityNotFoundException, UnauthorizedException {
    given(accountService.findById(1L)).willReturn(account);
    given(addressService.findById(1L)).willReturn(address);
    given(paymentTypeService.findById(1L)).willReturn(paymentType);
    given(paymentMethodRepo.findById(paymentMethod.getId())).willReturn(Optional.of(paymentMethod));
    PaymentMethod actualPaymentMethod =
        paymentMethodService.update(
            account.getId(), paymentMethod.getId(), paymentMethodDetails);
    assertThat(actualPaymentMethod).isEqualTo(paymentMethod);
  }

  @Test
  void updatePaymentMethodAccountNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(
            () ->
                paymentMethodService.update(
                    account.getId(), paymentMethod.getId(), paymentMethodDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
  }

  @Test
  void updatePaymentMethodNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(paymentMethodRepo.findById(1L)).willReturn(Optional.empty());
    assertThatThrownBy(
            () ->
                paymentMethodService.update(
                    account.getId(), paymentMethod.getId(), paymentMethodDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.PAYMENT_METHOD_NOT_FOUND.label, paymentMethod.getId()));
  }

  @Test
  void updatePaymentMethodTypeNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(paymentMethodRepo.findById(paymentMethod.getId())).willReturn(Optional.of(paymentMethod));
    given(paymentTypeService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(
                    ExceptionResponses.PAYMENT_TYPE_NOT_FOUND.label, paymentType.getId())));
    assertThatThrownBy(
            () ->
                paymentMethodService.update(
                    account.getId(), paymentMethod.getId(), paymentMethodDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(
            String.format(ExceptionResponses.PAYMENT_TYPE_NOT_FOUND.label, paymentType.getId()));
  }

  @Test
  void updatePaymentMethodAddressNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L)).willReturn(account);
    given(paymentMethodRepo.findById(paymentMethod.getId())).willReturn(Optional.of(paymentMethod));
    given(paymentTypeService.findById(1L)).willReturn(paymentType);
    given(addressService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ADDRESS_NOT_FOUND.label, address.getId())));
    assertThatThrownBy(
            () ->
                paymentMethodService.update(
                    account.getId(), paymentMethod.getId(), paymentMethodDetails))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ADDRESS_NOT_FOUND.label, address.getId()));
  }

  @Test
  void updatePaymentMethodUnauthorized() throws EntityNotFoundException {
    Account newAccount = new Account();
    newAccount.setId(2L);
    given(accountService.findById(2L)).willReturn(newAccount);
    given(paymentMethodRepo.findById(paymentMethod.getId())).willReturn(Optional.of(paymentMethod));
    assertThatThrownBy(
            () ->
                paymentMethodService.update(
                    newAccount.getId(), paymentMethod.getId(), paymentMethodDetails))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.PAYMENT_METHOD_UNAUTHORIZED.label,
                paymentMethod.getId(),
                newAccount.getId()));
  }

  @Test
  void getFavorite() throws EntityNotFoundException {
    paymentMethod.setFavorite(true);
    given(accountService.findById(1L)).willReturn(account);
    given(paymentMethodRepo.findFavoritePaymentMethodByAccount(account))
        .willReturn(Optional.of(paymentMethod));
    PaymentMethod actualPaymentMethod = paymentMethodService.getFavorite(account.getId());
    assertThat(actualPaymentMethod).isEqualTo(paymentMethod);
  }

  @Test
  void getFavoriteAccountNotFound() throws EntityNotFoundException {
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(() -> paymentMethodService.getFavorite(account.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
  }

  @Test
  void getFavoritePaymentMethodNotFound() throws EntityNotFoundException {
    given(accountService.findById(any())).willReturn(account);
    given(paymentMethodRepo.findFavoritePaymentMethodByAccount(any())).willReturn(Optional.empty());
    assertThatThrownBy(() -> paymentMethodService.getFavorite(1L))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.NO_FAVORITE_PAYMENT_METHOD.label, paymentMethod.getId()));
  }

  @Test
  void setFavorite() throws EntityNotFoundException, UnauthorizedException {
    address.setId(1L);
    address.setAccount(account);
    given(accountService.findById(1L)).willReturn(account);
    given(paymentMethodRepo.findById(paymentMethod.getId())).willReturn(Optional.of(paymentMethod));
    paymentMethodService.setFavorite(account.getId(), paymentMethod.getId());
  }

  @Test
  void setFavoritePaymentMethodNotFound() {
    address.setId(1L);
    address.setAccount(account);
    given(paymentMethodRepo.findById(paymentMethod.getId())).willReturn(Optional.empty());
    assertThatThrownBy(
            () -> paymentMethodService.setFavorite(account.getId(), paymentMethod.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(
            String.format(ExceptionResponses.PAYMENT_METHOD_NOT_FOUND.label, account.getId()));
  }

  @Test
  void setFavoriteUnauthorized() {
    address.setId(1L);
    address.setAccount(account);
    Account newAccount = new Account();
    newAccount.setId(2L);
    given(paymentMethodRepo.findById(paymentMethod.getId())).willReturn(Optional.of(paymentMethod));
    assertThatThrownBy(() -> paymentMethodService.setFavorite(newAccount.getId(), address.getId()))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.PAYMENT_METHOD_UNAUTHORIZED.label,
                address.getId(),
                newAccount.getId()));
  }

  @Test
  void setFavoriteAccountNotFound() throws EntityNotFoundException {
    given(paymentMethodRepo.findById(paymentMethod.getId())).willReturn(Optional.of(paymentMethod));
    given(accountService.findById(1L))
        .willThrow(
            new EntityNotFoundException(
                String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId())));
    assertThatThrownBy(() -> paymentMethodService.setFavorite(account.getId(), address.getId()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.ACCOUNT_NOT_FOUND.label, account.getId()));
  }

  @Test
  void deleteById() throws EntityNotFoundException, UnauthorizedException {
    given(paymentMethodRepo.findById(any())).willReturn(Optional.of(paymentMethod));
    paymentMethodService.delete(account.getId(), paymentMethod.getId());
    verify(paymentMethodRepo).delete(paymentMethod);
  }

  @Test
  void deleteByIdUnauthorized() {
    given(paymentMethodRepo.findById(any())).willReturn(Optional.of(paymentMethod));
    assertThatThrownBy(() -> paymentMethodService.delete(2L, paymentMethod.getId()))
        .isInstanceOf(UnauthorizedException.class)
        .hasMessage(
            String.format(
                ExceptionResponses.PAYMENT_METHOD_UNAUTHORIZED.label,
                paymentMethod.getId(),
                2L));
  }
}
