package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.UsernameTakenException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "api/v1/accounts")
public class AccountController {

  private final AccountService accountService;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public ResponseEntity<List<Account>> getAll() {
    return ResponseEntity.ok(accountService.getAll());
  }

  @PutMapping(path = "{account_id}")
  public ResponseEntity<Account> update(
      @PathVariable("account_id") Long account_id, @RequestBody AccountDetails accountDetails) {
    try {
      return ResponseEntity.ok(accountService.update(account_id, accountDetails));
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (UsernameTakenException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    }
  }

  @DeleteMapping(path = "{account_id}")
  public ResponseEntity<String> delete(@PathVariable("account_id") Long account_id) {
    try {
      accountService.delete(account_id);
      return ResponseEntity.ok("Deleted account");
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
