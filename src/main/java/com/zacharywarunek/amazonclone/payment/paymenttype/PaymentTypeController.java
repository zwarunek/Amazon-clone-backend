package com.zacharywarunek.amazonclone.payment.paymenttype;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
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
@RequestMapping(path = "api/v1/payment-types")
public class PaymentTypeController {
  PaymentTypeService paymentTypeService;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<PaymentType> create(@RequestBody PaymentType paymentType) {
    try {
      return ResponseEntity.ok(paymentTypeService.create(paymentType));
    } catch (BadRequestException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
  }

  @GetMapping
  public List<PaymentType> getAll() {
    return paymentTypeService.getAll();
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PutMapping(path = "{payment_type_id}")
  public ResponseEntity<Object> update(
      @PathVariable("payment_type_id") Long paymentMethodId,
      @RequestBody PaymentType paymentTypeDetails) {
    try {
      return ResponseEntity.ok(paymentTypeService.update(paymentMethodId, paymentTypeDetails));
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @DeleteMapping(path = "{payment_type_id}")
  public ResponseEntity<String> delete(@PathVariable("payment_type_id") Long paymentMethodId) {
    try {
      paymentTypeService.delete(paymentMethodId);
      return ResponseEntity.ok("Deleted payment type");
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
