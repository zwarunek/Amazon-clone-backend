package com.zacharywarunek.amazonclone.payment.paymenttype;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.PAYMENT_TYPE_ID_NOT_FOUND;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentTypeService {
  PaymentTypeRepo paymentTypeRepo;

  public List<PaymentType> getAll() {
    return paymentTypeRepo.findAll();
  }

  public PaymentType findById(Long paymentTypeId) throws EntityNotFoundException {
    return paymentTypeRepo
        .findById(paymentTypeId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format(PAYMENT_TYPE_ID_NOT_FOUND.label, paymentTypeId)));
  }

  public void create(PaymentType paymentTypeDetails) throws BadRequestException {
    if (paymentTypeDetails.getName() == null || paymentTypeDetails.getSrc() == null)
      throw new BadRequestException(ExceptionResponses.NULL_VALUES.label);
    paymentTypeRepo.save(paymentTypeDetails);
  }

  @Transactional
  public PaymentType update(Long paymentTypeId, PaymentType paymentTypeDetails)
      throws EntityNotFoundException {
    PaymentType paymentType = findById(paymentTypeId);
    if (paymentTypeDetails.getName() != null) {
      paymentType.setName(paymentTypeDetails.getName());
    }
    if (paymentTypeDetails.getSrc() != null) {
      paymentType.setSrc(paymentTypeDetails.getSrc());
    }
    return paymentType;
  }

  public void delete(Long paymentTypeId) throws EntityNotFoundException {
    paymentTypeRepo.delete(findById(paymentTypeId));
  }
}
