package com.zacharywarunek.amazonclone.payment.paymentmethod;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.address.Address;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentType;
import java.util.Collections;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class PaymentMethodControllerTest {

  @MockBean
  PaymentMethodService paymentMethodService;
  @Autowired
  private MockMvc mvc;
  private ObjectMapper mapper;
  private PaymentMethod paymentMethod;
  private PaymentMethodDetails paymentMethodDetails;

  @BeforeEach
  void before() {
    mapper = new ObjectMapper();
    Account account =
        new Account("Zach", "Warunek", "Zach@gmail.com", "password1234", AccountRole.ROLE_USER);
    Address address =
        new Address(account, "testAddress1", "testCity1", "MI", 12345, false, "Zach", "Warunek");
    PaymentType paymentType = new PaymentType("NAME", "SRC");
    paymentMethod =
        new PaymentMethod(
            account, paymentType, "NAME", "1111222233334444", "12/12", "123", false, address);
    paymentMethodDetails =
        new PaymentMethodDetails(1L, "NAME", "1111222233334444", "12/12", "123", 1L);
    account.setId(1L);
    address.setId(1L);
    paymentType.setId(1L);
    paymentMethod.setId(1L);
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
    given(paymentMethodService.getAll(any())).willReturn(Collections.singletonList(paymentMethod));
    mvc.perform(get("/api/v1/accounts/1/payment-methods").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(Collections.singletonList(
            paymentMethod))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getAllNotFound() throws Exception {
    when(paymentMethodService.getAll(any()))
        .thenThrow(new EntityNotFoundException("ACCOUNT NOT FOUND"));
    mvc.perform(get("/api/v1/accounts/1/payment-methods").accept(MediaType.APPLICATION_JSON))
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
    paymentMethod.setAccount(null);
    given(paymentMethodService.create(any(), any())).willReturn(paymentMethod);
    mvc.perform(
            post("/api/v1/accounts/1/payment-methods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentMethod)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(paymentMethod)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void createNotFound() throws Exception {
    paymentMethod.setAccount(null);
    when(paymentMethodService.create(any(), any()))
        .thenThrow(new EntityNotFoundException("ACCOUNT NOT FOUND"));
    mvc.perform(
            post("/api/v1/accounts/1/payment-methods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentMethod)))
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
    paymentMethod.setAccount(null);
    when(paymentMethodService.create(any(), any()))
        .thenThrow(new BadRequestException("NULL VALUES"));
    mvc.perform(
            post("/api/v1/accounts/1/payment-methods")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentMethod)))
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
    given(paymentMethodService.update(any(), any(), any())).willReturn(paymentMethod);
    mvc.perform(
            put("/api/v1/accounts/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentMethodDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(paymentMethod)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void updateNotFound() throws Exception {
    given(paymentMethodService.update(any(), any(), any()))
        .willThrow(new EntityNotFoundException("NOT FOUND"));
    mvc.perform(
            put("/api/v1/accounts/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentMethodDetails)))
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
    given(paymentMethodService.update(any(), any(), any()))
        .willThrow(new UnauthorizedException("NOT AUTHORIZED"));
    mvc.perform(
            put("/api/v1/accounts/1/payment-methods/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentMethodDetails)))
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
    given(paymentMethodService.getFavorite(any())).willReturn(paymentMethod);
    mvc.perform(get("/api/v1/accounts/1/payment-methods/favorite").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(paymentMethod)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void getFavoriteNotFound() throws Exception {
    when(paymentMethodService.getFavorite(any()))
        .thenThrow(new EntityNotFoundException("ACCOUNT NOT FOUND"));
    mvc.perform(get("/api/v1/accounts/1/payment-methods/favorite").accept(MediaType.APPLICATION_JSON))
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
    paymentMethod.setId(1L);
    mvc.perform(
            put("/api/v1/accounts/1/payment-methods/1/favorite"))
        .andExpect(status().isOk())
        .andExpect(
            content().string("Payment method with id " + paymentMethod.getId() + " is now the favorite payment method"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void setFavoriteNotFound() throws Exception {
    doThrow(new EntityNotFoundException("NOT FOUND"))
        .when(paymentMethodService)
        .setFavorite(any(), any());
    mvc.perform(
            put("/api/v1/accounts/1/payment-methods/1/favorite"))
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
        .when(paymentMethodService)
        .setFavorite(any(), any());
    mvc.perform(
            put("/api/v1/accounts/1/payment-methods/1/favorite"))
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
    paymentMethod.setId(1L);
    mvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/accounts/1/payment-methods/1"))
        .andExpect(status().isOk())
        .andExpect(
            content().string("Deleted payment method"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void deleteNotFound() throws Exception {
    doThrow(new EntityNotFoundException("NOT FOUND"))
        .when(paymentMethodService)
        .delete(any(), any());
    mvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/accounts/1/payment-methods/1"))
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
        .when(paymentMethodService)
        .delete(any(), any());
    mvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/accounts/1/payment-methods/1"))
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
        .andExpect(status().reason("UNAUTHORIZED"))
        .andReturn()
        .getResponse();
  }

}
