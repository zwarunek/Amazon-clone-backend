package com.zacharywarunek.amazonclone.payment.paymenttype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
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
class PaymentTypeServiceTest {

  @Mock PaymentTypeRepo paymentTypeRepo;
  @InjectMocks PaymentTypeService paymentTypeService;
  private PaymentType paymentType;

  @BeforeEach
  void setupAccount() {
    paymentType = new PaymentType("NAME", "SRC");
  }

  @Test
  void getAllAddresses() {
    given(paymentTypeRepo.findAll()).willReturn(Collections.singletonList(paymentType));
    List<PaymentType> paymentTypes = paymentTypeService.getAll();
    assertThat(paymentTypes).isEqualTo(Collections.singletonList(paymentType));
  }

  @Test
  void getByIdFound() throws EntityNotFoundException {
    given(paymentTypeRepo.findById(any())).willReturn(Optional.of(paymentType));
    assertThat(paymentTypeService.findById(any())).isEqualTo(paymentType);
  }

  @Test
  void getByIdNotFound() {
    given(paymentTypeRepo.findById(any())).willReturn(Optional.empty());
    assertThatThrownBy(() -> paymentTypeService.findById(any()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(
            String.format(ExceptionResponses.PAYMENT_TYPE_NOT_FOUND.label, paymentType.getId()));
  }

  @Test
  void createPaymentType() throws BadRequestException {
    paymentTypeService.create(paymentType);
    verify(paymentTypeRepo).save(paymentType);
  }

  @Test
  void createPaymentTypeNullValues() {
    assertThatThrownBy(() -> paymentTypeService.create(new PaymentType(null, null)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(ExceptionResponses.NULL_VALUES.label);
    verify(paymentTypeRepo, never()).save(any());
  }

  @Test
  void updatePaymentType() throws EntityNotFoundException {
    PaymentType paymentTypeDetails = new PaymentType("ChangedName", "ChangedSrc");
    paymentType.setId(1L);
    given(paymentTypeRepo.findById(paymentType.getId())).willReturn(Optional.of(paymentType));
    PaymentType type =
        paymentTypeService.update(paymentType.getId(), paymentTypeDetails);
    paymentTypeDetails.setId(paymentType.getId());
    assertThat(type).usingRecursiveComparison().isEqualTo(paymentTypeDetails);
  }

  @Test
  void updatePaymentTypeNotFound() {
    paymentType.setId(1L);
    given(paymentTypeRepo.findById(paymentType.getId())).willReturn(Optional.empty());
    assertThatThrownBy(() -> paymentTypeService.update(paymentType.getId(), any()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(
            String.format(ExceptionResponses.PAYMENT_TYPE_NOT_FOUND.label, paymentType.getId()));
  }

  @Test
  void deleteById() throws EntityNotFoundException {
    paymentType.setId(1L);
    given(paymentTypeRepo.findById(any())).willReturn(Optional.of(paymentType));
    paymentTypeService.delete(paymentType.getId());
    verify(paymentTypeRepo).delete(paymentType);
  }
}
