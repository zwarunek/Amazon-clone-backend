package com.zacharywarunek.amazonclone.payment.paymenttype;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.PAYMENT_TYPE_ID_NOT_FOUND;

@Service
@AllArgsConstructor
public class PaymentTypeService {
  PaymentTypeRepo paymentTypeRepo;

  public List<PaymentType> getAllPaymentTypes() {
    return paymentTypeRepo.findAll();
  }

  public Optional<PaymentType> getPaymentTypeById(Long paymentTypeId) {
    return paymentTypeRepo.findById(paymentTypeId);
  }

  public PaymentType createPaymentTypeById(PaymentType paymentTypeDetails)
      throws BadRequestException {
    if (paymentTypeDetails.getName() == null || paymentTypeDetails.getSrc() == null)
      throw new BadRequestException(ExceptionResponses.NULL_VALUES.label);
    return paymentTypeRepo.save(paymentTypeDetails);
  }

  @Transactional
  public void updatePaymentType(Long paymentTypeId, PaymentType paymentTypeDetails)
      throws EntityNotFoundException {

    PaymentType paymentType =
        paymentTypeRepo
            .findById(paymentTypeId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format(PAYMENT_TYPE_ID_NOT_FOUND.label, paymentTypeId)));
    if (paymentTypeDetails.getName() != null) {
      paymentType.setName(paymentTypeDetails.getName());
    }
    if (paymentTypeDetails.getSrc() != null) {
      paymentType.setSrc(paymentTypeDetails.getSrc());
    }
  }

  public void deletePaymentType(Long paymentTypeId) throws EntityNotFoundException {
    PaymentType paymentType =
        paymentTypeRepo
            .findById(paymentTypeId)
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format(PAYMENT_TYPE_ID_NOT_FOUND.label, paymentTypeId)));
    paymentTypeRepo.delete(paymentType);
  }
}
