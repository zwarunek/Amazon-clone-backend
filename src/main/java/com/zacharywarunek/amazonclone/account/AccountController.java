package com.zacharywarunek.amazonclone.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PutMapping(path = "{account_id}")
    public ResponseEntity<Object> updateAccount(@PathVariable("account_id") int account_id,
                                                @RequestBody Account account) {
        return accountService.updateAccount(account_id, account);
    }

    @DeleteMapping(path = "{account_id}")
    public ResponseEntity<Object> deleteAccount(@PathVariable("account_id") int account_id) {
        return accountService.deleteAccount(account_id);
    }
}
