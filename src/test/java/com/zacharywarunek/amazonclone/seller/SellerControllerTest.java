package com.zacharywarunek.amazonclone.seller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class SellerControllerTest {
  @MockBean
  SellerService sellerService;
  @Autowired
  private MockMvc mvc;
  private ObjectMapper mapper;
  private Seller seller;
  private Seller sellerDetails;

  @BeforeEach
  void before() {
    mapper = new ObjectMapper();
    seller = new Seller("category");
    seller.setId(1L);
    sellerDetails = new Seller("new category");
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
    given(sellerService.getAll()).willReturn(Collections.singletonList(seller));
    mvc.perform(get("/api/v1/sellers").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(Collections.singletonList(seller))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void create() throws Exception {
    given(sellerService.create(any())).willReturn(seller);
    mvc.perform(
            post("/api/v1/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seller)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(seller)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void createNullValues() throws Exception {
    sellerDetails.setName(null);
    when(sellerService.create(any()))
        .thenThrow(new BadRequestException("NULL VALUES"));
    mvc.perform(
            post("/api/v1/sellers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(seller)))
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
    given(sellerService.update(any(), any())).willReturn(sellerDetails);
    mvc.perform(
            put("/api/v1/sellers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sellerDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(sellerDetails)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void updateNotFound() throws Exception {
    given(sellerService.update(any(), any()))
        .willThrow(new EntityNotFoundException("NOT FOUND"));
    mvc.perform(
            put("/api/v1/sellers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sellerDetails)))
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
            delete("/api/v1/sellers/1"))
        .andExpect(status().isOk())
        .andExpect(
            content().string("Deleted seller"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void deleteNotFound() throws Exception {
    doThrow(new EntityNotFoundException("NOT FOUND"))
        .when(sellerService)
        .delete(any());
    mvc.perform(
            delete("/api/v1/sellers/1"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("NOT FOUND"))
        .andReturn()
        .getResponse();
  }
}
