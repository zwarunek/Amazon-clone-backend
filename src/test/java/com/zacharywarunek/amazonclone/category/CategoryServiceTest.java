package com.zacharywarunek.amazonclone.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock CategoryRepo categoryRepo;
  @InjectMocks CategoryService categoryService;
  private Category category;

  @BeforeEach
  void setupAccount() {
    category = new Category("NAME");
  }

  @Test
  void getAllAddresses() {
    given(categoryRepo.findAll((Sort) any())).willReturn(Collections.singletonList(category));
    List<Category> categories = categoryService.getAll();
    assertThat(categories).isEqualTo(Collections.singletonList(category));
  }

  @Test
  void getByIdFound() throws EntityNotFoundException {
    given(categoryRepo.findById(any())).willReturn(Optional.of(category));
    assertThat(categoryService.findById(any())).isEqualTo(category);
  }

  @Test
  void getByIdNotFound() {
    given(categoryRepo.findById(any())).willReturn(Optional.empty());
    assertThatThrownBy(() -> categoryService.findById(any()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.CATEGORY_NOT_FOUND.label, category.getId()));
  }

  @Test
  void createPaymentType() throws BadRequestException {
    categoryService.create(category);
    verify(categoryRepo).save(category);
  }

  @Test
  void createPaymentTypeNullValues() {
    assertThatThrownBy(() -> categoryService.create(new Category(null)))
        .isInstanceOf(BadRequestException.class)
        .hasMessage(ExceptionResponses.NULL_VALUES.label);
    verify(categoryRepo, never()).save(any());
  }

  @Test
  void updatePaymentType() throws EntityNotFoundException {
    Category categoryDetails = new Category("ChangedName");
    category.setId(1L);
    given(categoryRepo.findById(category.getId())).willReturn(Optional.of(category));
    Category categoryUpdated = categoryService.update(category.getId(), categoryDetails);
    categoryDetails.setId(category.getId());
    assertThat(categoryUpdated).usingRecursiveComparison().isEqualTo(categoryDetails);
  }

  @Test
  void updatePaymentTypeNotFound() {
    category.setId(1L);
    given(categoryRepo.findById(category.getId())).willReturn(Optional.empty());
    assertThatThrownBy(() -> categoryService.update(category.getId(), any()))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(String.format(ExceptionResponses.CATEGORY_NOT_FOUND.label, category.getId()));
  }

  @Test
  void deleteById() throws EntityNotFoundException {
    category.setId(1L);
    given(categoryRepo.findById(any())).willReturn(Optional.of(category));
    categoryService.delete(category.getId());
    verify(categoryRepo).delete(category);
  }
}
