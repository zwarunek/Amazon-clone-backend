package com.zacharywarunek.amazonclone.payment.paymentmethod;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.NULL_VALUES;
import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.PAYMENT_METHOD_ID_NOT_FOUND;
import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.PAYMENT_METHOD_UNAUTHORIZED;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.address.AddressService;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentTypeService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentMethodService {
  PaymentMethodRepo paymentMethodRepo;
  AccountService accountService;
  PaymentTypeService paymentTypeService;
  AddressService addressService;

  public List<PaymentMethod> getAll(Long accountId) throws EntityNotFoundException {
    return paymentMethodRepo.findByAccount(accountService.findById(accountId));
  }

  public PaymentMethod findById(Long paymentMethodId) throws EntityNotFoundException {
    return paymentMethodRepo
        .findById(paymentMethodId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format(PAYMENT_METHOD_ID_NOT_FOUND.label, paymentMethodId)));
  }

  public PaymentMethod create(Long accountId, PaymentMethodDetails paymentMethodDetails)
      throws BadRequestException, EntityNotFoundException {
    if (paymentMethodDetails.getPaymentTypeId() == null
        || paymentMethodDetails.getName() == null
        || paymentMethodDetails.getNumber() == null
        || paymentMethodDetails.getExp() == null
        || paymentMethodDetails.getCvv() == null
        || paymentMethodDetails.getAddressId() == null)
      throw new BadRequestException(NULL_VALUES.label);
    PaymentMethod paymentMethod =
        new PaymentMethod(
            accountService.findById(accountId),
            paymentTypeService.findById(paymentMethodDetails.getPaymentTypeId()),
            paymentMethodDetails.getName(),
            paymentMethodDetails.getNumber(),
            paymentMethodDetails.getExp(),
            paymentMethodDetails.getCvv(),
            false,
            addressService.findById(paymentMethodDetails.getAddressId()));
    return paymentMethodRepo.save(paymentMethod);
  }

  @Transactional
  public PaymentMethod update(
      Long accountId, Long paymentMethodId, PaymentMethodDetails paymentMethodDetails)
      throws EntityNotFoundException, UnauthorizedException {
    Account account = accountService.findById(accountId);
    PaymentMethod paymentMethod = findById(paymentMethodId);
    if (!paymentMethod.getAccount().getId().equals(account.getId()))
      throw new UnauthorizedException(
          String.format(PAYMENT_METHOD_UNAUTHORIZED.label, paymentMethodId, accountId));
    if (paymentMethodDetails.getPaymentTypeId() != null)
      paymentMethod.setPaymentType(
          paymentTypeService.findById(paymentMethodDetails.getPaymentTypeId()));
    if (paymentMethodDetails.getName() != null)
      paymentMethod.setName(paymentMethodDetails.getName());
    if (paymentMethodDetails.getName() != null)
      paymentMethod.setName(paymentMethodDetails.getName());
    if (paymentMethodDetails.getName() != null)
      paymentMethod.setName(paymentMethodDetails.getName());
    if (paymentMethodDetails.getName() != null)
      paymentMethod.setName(paymentMethodDetails.getName());
    if (paymentMethodDetails.getName() != null)
      paymentMethod.setName(paymentMethodDetails.getName());
    if (paymentMethodDetails.getAddressId() != null)
      paymentMethod.setAddress(addressService.findById(paymentMethodDetails.getAddressId()));
    return paymentMethod;
  }

  public PaymentMethod getFavorite(Long account_id) throws EntityNotFoundException {
    return paymentMethodRepo
        .findFavoritePaymentMethodByAccount(accountService.findById(account_id))
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format(
                        ExceptionResponses.NO_FAVORITE_PAYMENT_METHOD.label, account_id)));
  }

  public void setFavorite(Long account_id, Long paymentMethodId)
      throws EntityNotFoundException, UnauthorizedException {
    PaymentMethod paymentMethod = findById(paymentMethodId);
    if (!paymentMethod.getAccount().getId().equals(account_id))
      throw new UnauthorizedException(
          String.format(PAYMENT_METHOD_UNAUTHORIZED.label, paymentMethodId, account_id));
    paymentMethodRepo.resetFavorite(accountService.findById(account_id));
    paymentMethodRepo.setFavorite(paymentMethodId);
  }

  public void delete(Long paymentMethodId) throws EntityNotFoundException {
    paymentMethodRepo.delete(findById(paymentMethodId));
  }
}
