package com.zacharywarunek.amazonclone.payment.paymentmethod;

import static org.assertj.core.api.Assertions.assertThat;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.address.Address;
import com.zacharywarunek.amazonclone.address.AddressRepo;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentType;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentTypeRepo;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PaymentMethodRepoTest {

  @Autowired AccountRepo accountRepo;
  @Autowired PaymentTypeRepo paymentTypeRepo;
  @Autowired AddressRepo addressRepo;
  @Autowired PaymentMethodRepo paymentMethodRepo;
  private Account account;
  private PaymentMethod paymentMethod1;
  private PaymentMethod paymentMethod2;

  @BeforeEach
  void setup() {
    account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    accountRepo.save(account);
    PaymentType paymentType = new PaymentType("Visa", "SRC OF IMAGE");
    paymentTypeRepo.save(paymentType);
    Address address =
        new Address(account, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");
    addressRepo.save(address);
    paymentMethod1 =
        new PaymentMethod(
            account, paymentType, "NAME", "1111222233334444", "12/12", "123", false, address);
    paymentMethodRepo.save(paymentMethod1);
    paymentMethod2 =
        new PaymentMethod(
            account, paymentType, "NAME2", "5555666677778888", "11/11", "456", false, address);
    paymentMethodRepo.save(paymentMethod2);
  }

  @AfterEach
  void tearDown() {
    accountRepo.deleteAll();
    paymentTypeRepo.deleteAll();
    addressRepo.deleteAll();
    paymentMethodRepo.deleteAll();
  }

  @Test
  void findAddressByAccount() {
    List<PaymentMethod> paymentMethodList = paymentMethodRepo.findByAccount(account);
    assertThat(paymentMethodList).containsAll(Arrays.asList(paymentMethod1, paymentMethod2));
  }

  @Test
  void deleteAllByAccount() {
    paymentMethodRepo.deleteAllAtAccount(account);
    assertThat(paymentMethodRepo.findFavoritePaymentMethodByAccount(account)).isEmpty();
  }

  @Test
  void getFavoriteByAccount() {
    assertThat(paymentMethod1.getFavorite()).isFalse();
    paymentMethod1.setFavorite(true);
    paymentMethodRepo.save(paymentMethod1);
    Optional<PaymentMethod> found = paymentMethodRepo.findFavoritePaymentMethodByAccount(account);
    assertThat(found.isPresent()).isTrue();
    assertThat(found.get()).isEqualTo(paymentMethod1);
  }

  @Test
  void setFavorite() {
    assertThat(paymentMethod1.getFavorite()).isFalse();
    paymentMethodRepo.setFavorite(paymentMethod1.getId());
    Optional<PaymentMethod> found = paymentMethodRepo.findById(paymentMethod1.getId());
    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getFavorite()).isTrue();
  }

  @Test
  void resetFavorite() {
    assertThat(paymentMethod1.getFavorite()).isFalse();
    paymentMethod1.setFavorite(true);
    paymentMethodRepo.save(paymentMethod1);
    Optional<PaymentMethod> found = paymentMethodRepo.findById(paymentMethod1.getId());
    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getFavorite()).isTrue();
    paymentMethodRepo.resetFavorite(account);
    found = paymentMethodRepo.findById(paymentMethod1.getId());
    assertThat(found.isPresent()).isTrue();
    assertThat(found.get().getFavorite()).isFalse();
  }
}
