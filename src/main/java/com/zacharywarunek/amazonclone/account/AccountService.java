package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.ResponseObject;
import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.exeption.BadRequestException;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationToken;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import com.zacharywarunek.amazonclone.util.AuthRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class AccountService implements UserDetailsService {

    AccountRepo accountRepo;
    JwtUtil jwtUtil;
    private final ConfirmationTokenService confirmationTokenService;

    public List<Account> getAllAccounts(){
        return accountRepo.findAll();
    }
    public String register(Account account){
        if(account.getPassword() == null ||
                account.getUsername() == null ||
                account.getLast_name() == null ||
                account.getFirst_name() == null)
            throw new IllegalArgumentException("An error occurred when creating the account: Null values present");
        if (accountRepo.checkIfEmailExists(account.getUsername())){
            throw new BadRequestException("An account with that email " + account.getUsername() + " already exists");
        };
        account.setPassword(hashPassword(account.getPassword()));
        accountRepo.save(account);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                account
        );

        confirmationTokenService.saveConfirmationToken(
                confirmationToken);

        return token;
    }
    public ResponseEntity<Object> authenticate(AuthRequest authRequest){
        ResponseObject response = new ResponseObject();
        Account account;
        Optional<Account> accountOptional;
        try {

            accountOptional = accountRepo.findAccountByEmail(authRequest.getEmail());
            if(accountOptional.isPresent() && checkPassword(authRequest.getPassword(), accountOptional.get().getPassword())){
                account = accountOptional.get();
                Map<String, String> map = new HashMap<>();
                map.put("token", jwtUtil.generateToken(account));
                map.put("username", account.getUsername());
                map.put("first_name", account.getFirst_name());
                map.put("last_name", account.getLast_name());
                response.setStatus(HttpStatus.OK.value());
                response.setMessage("You are now logged in");
                response.setData(map);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            else{
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setMessage("Email or Password was incorrect");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("An error occurred when authenticating your account");
            response.setData("An error occurred when creating an account :  " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private String hashPassword(String password) {
        int workload = 15;
        String salt = BCrypt.gensalt(workload);

        return(BCrypt.hashpw(password, salt));
    }
    private boolean checkPassword(String password_plaintext, String stored_hash) {

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        return(BCrypt.checkpw(password_plaintext, stored_hash));
    }
    @Transactional
    public ResponseEntity<Object> updateAccount(int account_id, Account accountDetails) {
        Optional<Account> accountOptional = accountRepo.findById(account_id);
        Account account;
        ResponseObject response = new ResponseObject();
        if(!accountOptional.isPresent()){
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("Account with id " + account_id + " doesn't exist");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        else
            account = accountOptional.get();
        if (accountDetails.getFirst_name() != null && !accountDetails.getFirst_name().equals(account.getFirst_name())){
            account.setFirst_name(accountDetails.getFirst_name());
        }
        if (accountDetails.getLast_name() != null && !accountDetails.getLast_name().equals(account.getLast_name())){
            account.setLast_name(accountDetails.getLast_name());
        }
        if (accountDetails.getPassword() != null && !accountDetails.getPassword().equals(account.getPassword())){
            account.setPassword(hashPassword(accountDetails.getPassword()));
        }
        if (accountDetails.getUsername() != null && !accountDetails.getUsername().equals(account.getUsername())){
            if(!accountRepo.findAccountByEmail(accountDetails.getUsername()).isPresent()){
                account.setUsername(accountDetails.getUsername());
            }
            else
                throw new IllegalArgumentException("Email already is in use");
        }
        accountRepo.save(account);
        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Updated Account");
        response.setData(account);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public void deleteAccount(int account_id) {
        Optional<Account> account = accountRepo.findById(account_id);
        if(!account.isPresent()) {
            throw new IllegalStateException("account with id " + account_id + " does not exist");
        }
        confirmationTokenService.deleteAllAtAccountId(account.get());
        accountRepo.deleteById(account.get().getId());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Account> account = accountRepo.findAccountByEmail(username);
        if(account.isPresent())
            return new User(account.get().getUsername(), account.get().getPassword(), new ArrayList<>());
        throw new IllegalStateException("Account with email " + username + " exists");
    }

    public void enableAccount(String email) {
        accountRepo.enableAccount(email);
    }
}
