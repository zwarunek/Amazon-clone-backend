package com.zacharywarunek.amazonclone.seller;

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
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

  @Mock SellerRepo sellerRepo;
  @InjectMocks SellerService sellerService;
  private Seller seller;

  @BeforeEach
  void setupAccount() {
    seller = new Seller("NAME");
  }

  @Test
  void getAllAddresses() {
    given(sellerRepo.findAll((Sort) any())).willReturn(Collections.singletonList(seller));
    List<Seller> sellers = sellerService.getAll();
    assertThat(sellers).isEqualTo(Collections.singletonList(seller));
  }

  @Test
  void getByIdFound() throws EntityNotFoundException {
    given(sellerRepo.findById(any())).willReturn(Optional.of(seller));
    assertThat(sellerService.findById(any())).isEqualTo(seller);
  }

  @Test
  void getByIdNotFound() {
    given(sellerRepo.findById(any())).willReturn(Optional.empty());
    assertThatThrownBy(() -> sellerService.findById(any()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.SELLER_NOT_FOUND.label, seller.getId()));
  }

  @Test
  void createPaymentType() throws BadRequestException {
    sellerService.create(seller);
    verify(sellerRepo).save(seller);
  }

  @Test
  void createPaymentTypeNullValues() {
    assertThatThrownBy(() -> sellerService.create(new Seller(null)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(ExceptionResponses.NULL_VALUES.label);
    verify(sellerRepo, never()).save(any());
  }

  @Test
  void updatePaymentType() throws EntityNotFoundException {
    Seller sellerDetails = new Seller("ChangedName");
    seller.setId(1L);
    given(sellerRepo.findById(seller.getId())).willReturn(Optional.of(seller));
    Seller sellerUpdated = sellerService.update(seller.getId(), sellerDetails);
    sellerDetails.setId(seller.getId());
    assertThat(sellerUpdated).usingRecursiveComparison().isEqualTo(sellerDetails);
  }

  @Test
  void updatePaymentTypeNotFound() {
    seller.setId(1L);
    given(sellerRepo.findById(seller.getId())).willReturn(Optional.empty());
    assertThatThrownBy(() -> sellerService.update(seller.getId(), any()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.SELLER_NOT_FOUND.label, seller.getId()));
  }

  @Test
  void deleteById() throws EntityNotFoundException {
    seller.setId(1L);
    given(sellerRepo.findById(any())).willReturn(Optional.of(seller));
    sellerService.delete(seller.getId());
    verify(sellerRepo).delete(seller);
  }
}
