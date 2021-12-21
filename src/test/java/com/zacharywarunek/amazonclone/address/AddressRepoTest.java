package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AddressRepoTest {

  @Autowired AddressRepo addressRepo;
  @Autowired AccountRepo accountRepo;

  @Test
  void findAddressByAccount() {
    String email = "Zach@gmail.com";
    Account account =
        new Account(
            "Zach",
            "Warunek",
            email,
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    Address address1 =
        new Address(account, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");
    Address address2 =
        new Address(account, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");

    addressRepo.save(address1);
    addressRepo.save(address2);
    List<Address> addressList = addressRepo.findAddressByAccount(account);
    assertThat(addressList).containsAll(Arrays.asList(address1, address2));
  }

  @Test
  void findAddressByAccountEmpty() {
    String email = "Zach@gmail.com";
    Account account =
        new Account(
            "Zach",
            "Warunek",
            email,
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    List<Address> addressList = addressRepo.findAddressByAccount(account);
    assertThat(addressList).isEmpty();
  }

  @Test
  void deleteAllByAccount() {
    String email = "Zach@gmail.com";
    Account account =
        new Account(
            "Zach",
            "Warunek",
            email,
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    Address address1 =
        new Address(account, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");
    Address address2 =
        new Address(account, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");
    Address address3 =
        new Address(account, "testAddress3", "testCity2", "MI", 12345, false, "Zach", "Warunek");

    addressRepo.save(address1);
    addressRepo.save(address2);
    addressRepo.save(address3);
    addressRepo.deleteAllAtAccount(account);
    assertThat(addressRepo.findAddressByAccount(account)).isEmpty();
  }

  @Test
  void setAsFavorite() {
    String email = "Zach@gmail.com";
    Account account =
        new Account(
            "Zach",
            "Warunek",
            email,
            "$2a$15$2oqrWMbqoddS.uypTtSXu.xOUlqypXwuocXM4Jb3t1NE4vH.CkuxW",
            AccountRole.ROLE_USER);
    accountRepo.save(account);
    Address address1 =
        new Address(account, "testAddress1", "testCity1", "MI", 12345, true, "Zach", "Warunek");
    Address address2 =
        new Address(account, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");
    Address address3 =
        new Address(account, "testAddress3", "testCity2", "MI", 12345, false, "Zach", "Warunek");

    addressRepo.save(address1);
    addressRepo.save(address2);
    addressRepo.save(address3);
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
