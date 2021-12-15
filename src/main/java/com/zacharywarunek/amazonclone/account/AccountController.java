package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.util.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @PostMapping(path = "/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody AuthRequest authRequest) {
        return accountService.authenticate(authRequest);
    }

    @PutMapping(path = "{account_id}")
    public ResponseEntity<Object> updateAccount(@PathVariable("account_id") int account_id,
                                                @RequestBody Account account) {
        return accountService.updateAccount(account_id, account);
    }

    @DeleteMapping(path = "{account_id}")
    public void deleteAccount(@PathVariable("account_id") int account_id) {
        accountService.deleteAccount(account_id);
    }
}
