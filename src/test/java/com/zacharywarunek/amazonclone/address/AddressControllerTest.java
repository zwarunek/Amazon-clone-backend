package com.zacharywarunek.amazonclone.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AddressControllerTest {

  @Autowired AddressService addressService;
  @Autowired AddressRepo addressRepo;
  @Autowired AccountRepo accountRepo;
  @Autowired private MockMvc mvc;
  private ObjectMapper mapper;


  @AfterEach
  void tearDown() {
    addressRepo.deleteAll();
    accountRepo.deleteAll();
  }
  @BeforeEach
  void before() {
    mapper = new ObjectMapper();
    mapper.findAndRegisterModules();
  }

  @Test
  void shouldCreateMockMvc() {
    assertNotNull(mvc);
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getAllAddressesAdminSame() throws Exception {
    Account account1 =
        new Account("foo", "bar", "foo@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    Address address1 =
        new Address(account1, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");
    Address address2 =
        new Address(account1, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");
    accountRepo.save(account1);
    address1.setId(1L);
    address1.setAccount(account1);
    address2.setId(2L);
    address2.setAccount(account1);
    given(addressRepo.findAddressByAccount(account1)).willReturn(Arrays.asList(address1, address2));
    given(accountRepo.findById(1L)).willReturn(Optional.of(account1));
    mvc.perform(get("/api/v1/accounts/1/addresses").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(Arrays.asList(address1, address2))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getAllAddressesAdminOther() throws Exception {

    Account account1 =
        new Account("foo", "bar", "fooNotEqual@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    Address address1 =
        new Address(account1, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");
    Address address2 =
        new Address(account1, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");

    address1.setId(1L);
    address1.setAccount(account1);
    address2.setId(2L);
    address2.setAccount(account1);
    given(addressRepo.findAddressByAccount(account1)).willReturn(Arrays.asList(address1, address2));
    given(accountRepo.findById(1L)).willReturn(Optional.of(account1));
    mvc.perform(get("/api/v1/accounts/1/addresses").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(Arrays.asList(address1, address2))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"USER"})
  void getAllAddressesUserSame() throws Exception {
    Account account =
        new Account("foo", "bar", "foo@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    Address address1 =
        new Address(account, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");
    Address address2 =
        new Address(account, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");
    account.setId(1L);
    address1.setId(1L);
    address1.setAccount(account);
    address2.setId(2L);
    address2.setAccount(account);
    given(addressRepo.findAddressByAccount(account)).willReturn(Arrays.asList(address1, address2));
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(accountRepo.findAccountByUsername("foo@gmail.com")).willReturn(Optional.of(account));
    mvc.perform(get("/api/v1/accounts/1/addresses").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(Arrays.asList(address1, address2))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"USER"})
  void getAllAddressesUserOther() throws Exception {
    Account account =
        new Account("foo", "bar", "fooNotEqual@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(accountRepo.findAccountByUsername("fooNotEqual@gmail.com"))
        .willReturn(Optional.of(account));
    mvc.perform(get("/api/v1/accounts/1/addresses").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
        .andExpect(status().reason("Forbidden"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getAllAddressesAdminOtherNotFound() throws Exception {
    given(accountRepo.findById(1L)).willReturn(Optional.empty());
    mvc.perform(get("/api/v1/accounts/1/addresses").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("Account with id " + 1 + " not found"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void createAddressesAdmin() throws Exception {
    Address address =
        new Address(null, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");

    Account account =
        new Account("foo", "bar", "foodifferent@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    mvc.perform(
            post("/api/v1/accounts/1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(address)))
        .andExpect(status().isOk())
        .andExpect(content().string("Address Successfully Created"))
        .andReturn()
        .getResponse();
    address.setAccount(account);
    ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
    verify(addressRepo).save(addressCaptor.capture());
    assertThat(addressCaptor.getValue()).usingRecursiveComparison().isEqualTo(address);
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"USER"})
  void createAddressesUserSame() throws Exception {
    Address address =
        new Address(null, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");

    Account account =
        new Account("foo", "bar", "foo@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    account.setId(1L);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(accountRepo.findAccountByUsername("foo@gmail.com")).willReturn(Optional.of(account));
    mvc.perform(
            post("/api/v1/accounts/1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(address)))
        .andExpect(status().isOk())
        .andExpect(content().string("Address Successfully Created"))
        .andReturn()
        .getResponse();
    address.setAccount(account);
    ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
    verify(addressRepo).save(addressCaptor.capture());
    assertThat(addressCaptor.getValue()).usingRecursiveComparison().isEqualTo(address);
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"USER"})
  void createAddressesUserOther() throws Exception {
    Address address =
        new Address(null, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");

    Account account =
        new Account("foo", "bar", "foodifferent@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    account.setId(1L);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(accountRepo.findAccountByUsername("foo@gmail.com")).willReturn(Optional.of(account));
    mvc.perform(
            post("/api/v1/accounts/1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(address)))
        .andExpect(status().isOk())
        .andExpect(content().string("Address Successfully Created"))
        .andReturn()
        .getResponse();
    address.setAccount(account);
    ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
    verify(addressRepo).save(addressCaptor.capture());
    assertThat(addressCaptor.getValue()).usingRecursiveComparison().isEqualTo(address);
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void createAddressAdminOtherNotFound() throws Exception {
    Address address =
        new Address(null, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");

    Account account =
        new Account("foo", "bar", "foo@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    account.setId(1L);
    given(accountRepo.findById(1L)).willReturn(Optional.empty());
    given(accountRepo.findAccountByUsername("foo@gmail.com")).willReturn(Optional.of(account));
    mvc.perform(
            post("/api/v1/accounts/1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(address)))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("Account with id " + 1 + " not found"))
        .andReturn()
        .getResponse();
    verify(accountRepo, never()).save(any());
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"USER"})
  void createAddressesUserNullValues() throws Exception {
    Address address = new Address(null, null, null, "MI", 12345, false, "Zach", "Warunek");

    Account account =
        new Account("foo", "bar", "foo@gmail.com", "password1234", AccountRole.ROLE_ADMIN);
    account.setId(1L);
    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(accountRepo.findAccountByUsername("foo@gmail.com")).willReturn(Optional.of(account));
    mvc.perform(
            post("/api/v1/accounts/1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(address)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(
            status()
                .reason("An error occurred when creating the address: " + "Null values present"))
        .andReturn()
        .getResponse();
    verify(addressRepo, never()).save(any());
  }


  @Test
  @WithMockUser(
          username = "foo@gmail.com",
          roles = {"ADMIN"})
  void updateAddressesAdmin() throws Exception {
    Account account =
          new Account("foo", "bar", "foodifferent@gmail.com", "password1234", AccountRole.ROLE_ADMIN);

    Address address =
            new Address(account, "testAddress2", "testCity2", "MI", 12345, false, "Zach", "Warunek");
    Address addressDetails =
            new Address(null, "updated Address", null, "CA", 54321, null, null, null);

    given(accountRepo.findById(1L)).willReturn(Optional.of(account));
    given(addressRepo.findById(1L)).willReturn(Optional.of(address));
    mvc.perform(
                    put("/api/v1/accounts/1/addresses/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(addressDetails)))
            .andExpect(status().isOk())
            .andExpect(content().string("Address Successfully Created"))
            .andReturn()
            .getResponse();
    address.setAccount(account);
    ArgumentCaptor<Address> addressCaptor = ArgumentCaptor.forClass(Address.class);
    verify(addressRepo).save(addressCaptor.capture());
    assertThat(addressCaptor.getValue()).usingRecursiveComparison().ignoringActualNullFields().isEqualTo(address);
  }
}
