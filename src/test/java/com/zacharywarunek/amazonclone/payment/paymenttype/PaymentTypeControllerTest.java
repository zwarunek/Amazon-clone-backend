package com.zacharywarunek.amazonclone.payment.paymenttype;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
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
class PaymentTypeControllerTest {
  @MockBean PaymentTypeService paymentTypeService;
  @Autowired private MockMvc mvc;
  private ObjectMapper mapper;
  private PaymentType paymentType;
  private PaymentType paymentTypeDetails;

  @BeforeEach
  void before() {
    mapper = new ObjectMapper();
    paymentType = new PaymentType("VISA", "SRC IMAGE");
    paymentType.setId(1L);
    paymentTypeDetails = new PaymentType("DISCOVER", "SRC IMAGE DETAILS");
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
    given(paymentTypeService.getAll()).willReturn(Collections.singletonList(paymentType));
    mvc.perform(get("/api/v1/payment-types").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(Collections.singletonList(paymentType))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void create() throws Exception {
    given(paymentTypeService.create(any())).willReturn(paymentType);
    mvc.perform(
            post("/api/v1/payment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentType)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(paymentType)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void createNullValues() throws Exception {
    paymentTypeDetails.setName(null);
    when(paymentTypeService.create(any()))
        .thenThrow(new BadRequestException("NULL VALUES"));
    mvc.perform(
            post("/api/v1/payment-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentType)))
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
    given(paymentTypeService.update(any(), any())).willReturn(paymentTypeDetails);
    mvc.perform(
            put("/api/v1/payment-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentTypeDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(paymentTypeDetails)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void updateNotFound() throws Exception {
    given(paymentTypeService.update(any(), any()))
        .willThrow(new EntityNotFoundException("NOT FOUND"));
    mvc.perform(
            put("/api/v1/payment-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(paymentTypeDetails)))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("NOT FOUND"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void deleteTest() throws Exception {
    mvc.perform(
            delete("/api/v1/payment-types/1"))
        .andExpect(status().isOk())
        .andExpect(
            content().string("Deleted payment type"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void deleteNotFound() throws Exception {
    doThrow(new EntityNotFoundException("NOT FOUND"))
        .when(paymentTypeService)
        .delete(any());
    mvc.perform(
            delete("/api/v1/payment-types/1"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("NOT FOUND"))
        .andReturn()
        .getResponse();
  }

}
