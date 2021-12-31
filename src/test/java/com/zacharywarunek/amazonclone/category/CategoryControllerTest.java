package com.zacharywarunek.amazonclone.category;

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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class CategoryControllerTest {
  @MockBean
  CategoryService categoryService;
  @Autowired
  private MockMvc mvc;
  private ObjectMapper mapper;
  private Category category;
  private Category categoryDetails;

  @BeforeEach
  void before() {
    mapper = new ObjectMapper();
    category = new Category("category");
    category.setId(1L);
    categoryDetails = new Category("new category");
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
    given(categoryService.getAll()).willReturn(Collections.singletonList(category));
    mvc.perform(get("/api/v1/categories").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(Collections.singletonList(category))))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void create() throws Exception {
    given(categoryService.create(any())).willReturn(category);
    mvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(category)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void createNullValues() throws Exception {
    categoryDetails.setName(null);
    when(categoryService.create(any()))
        .thenThrow(new BadRequestException("NULL VALUES"));
    mvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(category)))
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
    given(categoryService.update(any(), any())).willReturn(categoryDetails);
    mvc.perform(
            put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(categoryDetails)))
        .andExpect(status().isOk())
        .andExpect(content().string(mapper.writeValueAsString(categoryDetails)))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void updateNotFound() throws Exception {
    given(categoryService.update(any(), any()))
        .willThrow(new EntityNotFoundException("NOT FOUND"));
    mvc.perform(
            put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(categoryDetails)))
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
            delete("/api/v1/categories/1"))
        .andExpect(status().isOk())
        .andExpect(
            content().string("Deleted category"))
        .andReturn()
        .getResponse();
  }

  @Test
  @WithMockUser(
      username = "foo@gmail.com",
      roles = {"ADMIN"})
  void deleteNotFound() throws Exception {
    doThrow(new EntityNotFoundException("NOT FOUND"))
        .when(categoryService)
        .delete(any());
    mvc.perform(
            delete("/api/v1/categories/1"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(status().reason("NOT FOUND"))
        .andReturn()
        .getResponse();
  }
}
