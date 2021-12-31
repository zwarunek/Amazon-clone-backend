package com.zacharywarunek.amazonclone.category;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentType;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentTypeService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "api/v1/categories")
public class CategoryController {
  CategoryService categoryService;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<Category> create(@RequestBody Category category) {
    try {
      return ResponseEntity.ok(categoryService.create(category));
    } catch (BadRequestException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  public ResponseEntity<List<Category>> getAll() {
    return ResponseEntity.ok().body(categoryService.getAll());
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping(path = "{category_id}")
  public ResponseEntity<Object> update(
      @PathVariable("category_id") Long categoryId,
      @RequestBody Category category) {
    try {
      return ResponseEntity.ok(categoryService.update(categoryId, category));
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @DeleteMapping(path = "{category_id}")
  public ResponseEntity<String> delete(@PathVariable("category_id") Long categoryId) {
    try {
      categoryService.delete(categoryId);
      return ResponseEntity.ok("Deleted category");
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
