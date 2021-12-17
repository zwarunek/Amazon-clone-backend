package com.zacharywarunek.amazonclone;

import com.zacharywarunek.amazonclone.account.AccountService;
import com.zacharywarunek.amazonclone.util.AuthRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1")
@AllArgsConstructor
public class BaseController {

    private final AccountService accountService;

    @RequestMapping(value = "/apiTest", method = RequestMethod.GET)
    public ResponseEntity<Object> apiTest() {
        return ResponseEntity.ok().body("API is functioning normally for environment: " + System.getenv("ENV"));
    }

    @RequestMapping(value = "/apiTestAuth", method = RequestMethod.GET)
    public ResponseEntity<Object> apiTestAuth() {
        return ResponseEntity.ok().body("API Auth is functioning normally for environment: " + System.getenv("ENV"));
    }

    @PostMapping(path = "/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody AuthRequest authRequest) {
        return accountService.authenticate(authRequest);
    }
}
