package com.zacharywarunek.amazonclone.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AddressControllerTest {

  @MockBean AddressService addressService;
  @Autowired private MockMvc mvc;
  private ObjectMapper mapper;
  private Address address;
  private Address addressDetails;

  @BeforeEach
  void before() {
    mapper = new ObjectMapper();
    Account account = new Account("Zach",
                                  "Warunek",
                                  "foo@gmail.com",
                                  "password1234",
                                  AccountRole.ROLE_USER);
    account.setId(1L);
    address =
        new Address(account, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");

    addressDetails =
        new Address(
            null,
            "changedAddress",
            "changedCity",
            "CA",
            54321,
            false,
            "changedFirst",
            "changedLast");
  }

  @Test
  void shouldCreateMockMvc() {
    assertNotNull(mvc);
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getAll() throws Exception {
    given(addressService.getAll(any())).willReturn(Collections.singletonList(address));
    mvc.perform(get("/api/v1/accounts/1/addresses").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(Collections.singletonList(address))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getAllNotFound() throws Exception {
    when(addressService.getAll(any()))
        .thenThrow(new EntityNotFoundException("ACCOUNT NOT FOUND"));
    mvc.perform(get("/api/v1/accounts/1/addresses").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("ACCOUNT NOT FOUND"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void create() throws Exception {
    address.setAccount(null);
    given(addressService.create(any(), any())).willReturn(address);
    mvc.perform(
            post("/api/v1/accounts/1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(address)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(address)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void createNotFound() throws Exception {
    address.setAccount(null);
    when(addressService.create(any(), any()))
        .thenThrow(new EntityNotFoundException("ACCOUNT NOT FOUND"));
    mvc.perform(
            post("/api/v1/accounts/1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(address)))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("ACCOUNT NOT FOUND"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void createNullValues() throws Exception {
    address.setAccount(null);
    when(addressService.create(any(), any()))
        .thenThrow(new BadRequestException("NULL VALUES"));
    mvc.perform(
            post("/api/v1/accounts/1/addresses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(address)))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(status().reason("NULL VALUES"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void update() throws Exception {
    given(addressService.update(any(), any(), any())).willReturn(addressDetails);
    mvc.perform(
            put("/api/v1/accounts/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addressDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(addressDetails)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void updateNotFound() throws Exception {
    given(addressService.update(any(), any(), any()))
        .willThrow(new EntityNotFoundException("NOT FOUND"));
    mvc.perform(
            put("/api/v1/accounts/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addressDetails)))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("NOT FOUND"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void updateUnauthorized() throws Exception {
    given(addressService.update(any(), any(), any()))
        .willThrow(new UnauthorizedException("NOT AUTHORIZED"));
    mvc.perform(
            put("/api/v1/accounts/1/addresses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(addressDetails)))
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
        .andExpect(status().reason("NOT AUTHORIZED"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getFavorite() throws Exception {
    given(addressService.getFavorite(any())).willReturn(address);
    mvc.perform(get("/api/v1/accounts/1/addresses/favorite").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(address)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getFavoriteNotFound() throws Exception {
    when(addressService.getFavorite(any()))
        .thenThrow(new EntityNotFoundException("ACCOUNT NOT FOUND"));
    mvc.perform(get("/api/v1/accounts/1/addresses/favorite").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("ACCOUNT NOT FOUND"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void setFavorite() throws Exception {
    address.setId(1L);
    mvc.perform(
            put("/api/v1/accounts/1/addresses/1/favorite"))
        .andExpect(status().isOk())
        .andExpect(
            content().string("Address with id " + address.getId() + " is now the favorite address"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void setFavoriteNotFound() throws Exception {
    doThrow(new EntityNotFoundException("NOT FOUND"))
        .when(addressService)
        .setFavorite(any(), any());
    mvc.perform(
            put("/api/v1/accounts/1/addresses/1/favorite"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("NOT FOUND"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void setFavoriteUnauthorized() throws Exception {
    doThrow(new UnauthorizedException("NOT AUTHORIZED"))
        .when(addressService)
        .setFavorite(any(), any());
    mvc.perform(
            put("/api/v1/accounts/1/addresses/1/favorite"))
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
        .andExpect(status().reason("NOT AUTHORIZED"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void delete() throws Exception {
    address.setId(1L);
    mvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/accounts/1/addresses/1"))
        .andExpect(status().isOk())
        .andExpect(
            content().string("Deleted address"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void deleteNotFound() throws Exception {
    doThrow(new EntityNotFoundException("NOT FOUND"))
        .when(addressService)
        .delete(any(), any());
    mvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/accounts/1/addresses/1"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("NOT FOUND"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void deleteUnauthorized() throws Exception {
    doThrow(new UnauthorizedException("UNAUTHORIZED"))
        .when(addressService)
        .delete(any(), any());
    mvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/accounts/1/addresses/1"))
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
        .andExpect(status().reason("UNAUTHORIZED"))
        .andReturn()
        .getResponse();
  }
}
