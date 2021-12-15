package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationToken;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import com.zacharywarunek.amazonclone.util.AuthRequest;
import com.zacharywarunek.amazonclone.util.BeansUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AccountService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    AccountRepo accountRepo;
    JwtUtil jwtUtil;

    @Autowired
    public AccountService(AccountRepo accountRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                          ConfirmationTokenService confirmationTokenService) {
        this.accountRepo = accountRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenService = confirmationTokenService;
    }


    public List<Account> getAllAccounts() {
        return accountRepo.findAll();
    }

    public String register(Account account) {
        if(account.getPassword() == null || account.getUsername() == null || account.getLast_name() == null ||
                account.getFirst_name() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "An error occurred when creating the account: Null values present");
        if(accountRepo.checkIfUsernameExists(account.getUsername()))
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "An account with that email " + account.getUsername() + " already exists");

        account.setPassword(passwordEncoder.encode(account.getPassword()));
        accountRepo.save(account);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        return token;
    }

    public ResponseEntity<Object> authenticate(AuthRequest authRequest) {
        if(authRequest.getUsername() == null || authRequest.getPassword() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'username' or 'password' fields not found");
        Optional<Account> accountOptional = accountRepo.findAccountByUsername(authRequest.getUsername());
        if(!accountOptional.isPresent() ||
                !passwordEncoder.matches(authRequest.getPassword(), accountOptional.get().getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Username or Password was incorrect");
        Account account = accountOptional.get();
        HttpHeaders headers = new HttpHeaders();
        String token = jwtUtil.generateToken(account);
        headers.add("Authorization", token);
        return ResponseEntity.ok().headers(headers).body("Authorization Successful");
    }

    @Transactional
    public ResponseEntity<Object> updateAccount(int account_id, Account accountDetails) {
        Optional<Account> accountOptional = accountRepo.findById(account_id);
        if(!accountOptional.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with id " + account_id + " doesn't exist");
        Account account = accountOptional.get();
        if(accountDetails.getUsername() != null && !accountDetails.getUsername().equals(account.getUsername()))
            if(accountRepo.findAccountByUsername(accountDetails.getUsername()).isPresent())
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already in use");
        if(accountDetails.getPassword() != null)
            accountDetails.setPassword(passwordEncoder.encode(accountDetails.getPassword()));

        BeansUtil<Account> beansUtil = new BeansUtil<>();
        beansUtil.copyNonNullProperties(account, accountDetails);
        accountRepo.save(account);
        Map<String, Object> map = new HashMap<>();
        map.put("status", HttpStatus.OK.value());
        map.put("message", "Updated Account");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    public void deleteAccount(int account_id) {
        Optional<Account> account = accountRepo.findById(account_id);
        if(!account.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with id " + account_id + " doesn't exist");
        confirmationTokenService.deleteAllAtAccountId(account.get());
        accountRepo.deleteById(account.get().getId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepo.findAccountByUsername(username);
        if(account.isPresent())
            return new User(account.get().getUsername(), account.get().getPassword(), account.get().getAuthorities());
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account with username " + username + " doesn't exist");
    }

    public void enableAccount(String username) {
        if(!accountRepo.checkIfUsernameExists(username)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Account with username " + username + " doesn't exist");
        accountRepo.enableAccount(username);
    }
}
