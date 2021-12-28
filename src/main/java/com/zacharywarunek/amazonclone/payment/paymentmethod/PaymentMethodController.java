package com.zacharywarunek.amazonclone.payment.paymentmethod;

import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping(path = "api/v1/accounts/{account_id}/payment-methods")
public class PaymentMethodController {
  PaymentMethodService paymentMethodService;

  @PostMapping
  public ResponseEntity<PaymentMethod> create(
      @PathVariable("account_id") Long accountId,
      @RequestBody PaymentMethodDetails paymentMethodDetails) {
    try {
      return ResponseEntity.ok(paymentMethodService.create(accountId, paymentMethodDetails));
    } catch (BadRequestException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @GetMapping
  public List<PaymentMethod> getAll(@PathVariable("account_id") Long accountId) {
    try {
      return paymentMethodService.getAll(accountId);
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping(path = "{payment_method_id}")
  public ResponseEntity<PaymentMethod> update(
      @PathVariable("account_id") Long accountId,
      @PathVariable("payment_method_id") Long paymentMethodId,
      @RequestBody PaymentMethodDetails paymentMethodDetails) {
    try {
      return ResponseEntity.ok(
          paymentMethodService.update(accountId, paymentMethodId, paymentMethodDetails));
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (UnauthorizedException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
  }

  @DeleteMapping(path = "{payment_method_id}")
  public ResponseEntity<String> delete(
      @PathVariable("payment_method_id") Long paymentMethodId,
      @PathVariable("account_id") Long accountId) {
    try {
      paymentMethodService.delete(paymentMethodId, accountId);
      return ResponseEntity.ok("Deleted address");
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (UnauthorizedException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
  }

  @GetMapping(path = "/favorite")
  public ResponseEntity<PaymentMethod> getFavorite(@PathVariable("account_id") Long accountId) {
    try {
      return ResponseEntity.ok(paymentMethodService.getFavorite(accountId));
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }

  @PutMapping(path = "{payment_method_id}/favorite")
  public ResponseEntity<String> setFavorite(
      @PathVariable("account_id") Long accountId,
      @PathVariable("payment_method_id") Long paymentMethodId) {
    try {
      paymentMethodService.setFavorite(accountId, paymentMethodId);
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (UnauthorizedException e) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
    return ResponseEntity.ok(
        "Payment method with id " + paymentMethodId + " is now the favorite payment method");
  }
}
