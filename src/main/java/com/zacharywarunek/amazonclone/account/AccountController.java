package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.UsernameTakenException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "api/v1/accounts")
public class AccountController {

  private final AccountService accountService;

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @GetMapping
  public ResponseEntity<List<Account>> getAllAccounts() {
    return ResponseEntity.ok(accountService.getAllAccounts());
  }

  @PutMapping(path = "{account_id}")
  public ResponseEntity<Account> updateAccount(
      @PathVariable("account_id") Long account_id, @RequestBody AccountDetails accountDetails) {
    try {
      return ResponseEntity.ok(accountService.updateAccount(account_id, accountDetails));
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    } catch (UsernameTakenException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
    }
  }

  @DeleteMapping(path = "{account_id}")
  public ResponseEntity<String> deleteAccount(@PathVariable("account_id") Long account_id) {
    try {
      accountService.deleteAccount(account_id);
      return ResponseEntity.ok("Deleted account");
    } catch (EntityNotFoundException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
    }
  }
}
