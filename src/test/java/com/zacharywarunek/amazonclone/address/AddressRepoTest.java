package com.zacharywarunek.amazonclone.address;

import static org.assertj.core.api.Assertions.assertThat;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AddressRepoTest {

  @Autowired AddressRepo addressRepo;
  @Autowired AccountRepo accountRepo;
  private Account account;
  private Address address1;
  private Address address2;

  @BeforeEach
  void setup() {
    account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    accountRepo.save(account);
    address1 =
        new Address(account, "testAddress1", "testCity1", "CA", 54321, false, "Zach1", "Warunek1");
    address2 =
        new Address(account, "testAddress2", "testCity2", "MI", 12345, false, "Zach2", "Warunek2");
    addressRepo.save(address1);
    addressRepo.save(address2);
  }

  @AfterEach
  void tearDown() {
    accountRepo.deleteAll();
    addressRepo.deleteAll();
  }

  @Test
  void findAddressByAccount() {
    List<Address> addressList = addressRepo.findAddressByAccount(account);
    assertThat(addressList).containsAll(Arrays.asList(address1, address2));
  }

  @Test
  void findAddressByAccountEmpty() {
    addressRepo.deleteAll();
    List<Address> addressList = addressRepo.findAddressByAccount(account);
    assertThat(addressList).isEmpty();
  }

  @Test
  void deleteAllByAccount() {
    addressRepo.save(address1);
    addressRepo.save(address2);
    addressRepo.deleteAllAtAccount(account);
    assertThat(addressRepo.findAddressByAccount(account)).isEmpty();
  }

  @Test
  void setAsFavorite() {
    address1.setFavorite(true);
    addressRepo.save(address1);
    addressRepo.resetFavorite(account);
    addressRepo.setFavorite(address2.getId());
    Optional<Address> addressOptional1 = addressRepo.findById(address1.getId());
    Optional<Address> addressOptional2 = addressRepo.findFavoriteAddressByAccount(account);
    assertThat(addressOptional1.isPresent()).isTrue();
    assertThat(addressOptional1.get().getFavorite()).isFalse();
    assertThat(addressOptional2.isPresent()).isTrue();
    assertThat(addressOptional2.get())
        .usingRecursiveComparison()
        .ignoringFields("favorite")
        .isEqualTo(address2);
    assertThat(addressOptional2.get().getFavorite()).isTrue();
  }
}
