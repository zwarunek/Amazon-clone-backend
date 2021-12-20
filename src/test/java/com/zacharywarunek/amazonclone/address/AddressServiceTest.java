package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.AccountRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class AddressServiceTest {
  @Mock private AddressRepo addressRepo;
  @Mock private AccountRepo accountRepo;

  private AddressService addressService;

  @BeforeEach
  void initUseCase() {
    addressService = new AddressService(addressRepo, accountRepo);
  }

  @Test
  void createAddressTest() {
//    Address address = new Address()
  }
}
