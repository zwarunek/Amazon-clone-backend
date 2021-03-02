package com.zacharywarunek.kettering.cs461project;

import com.zacharywarunek.kettering.cs461project.entitys.Account;
import com.zacharywarunek.kettering.cs461project.repositories.IAccountRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class Service {

    @Autowired
    IAccountRepo accountRepo;

    public ResponseObject CreateAccount(String payloadFromUI){
        ResponseObject response = new ResponseObject();
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        Account account = new Account();
        try {

            account = account.constructEntity(jsonPayload.getString("firstName"), jsonPayload.getString("lastName"),hashPassword(jsonPayload.getString("password")),jsonPayload.getBoolean("primeMember"),jsonPayload.getString("email"));
            accountRepo.save(account);
            response.setStatus(200);
            response.setMessage("Account has been created");
            response.setData("Account has been created for " + account.getFirstName() + " " + account.getLastName());
        }
        catch (Exception e){
            response.setStatus(411);
            response.setMessage("An error occurred when creating your account");
            response.setData("An error occurred when creating an account :  " + e.getMessage());
        }
        return response;
    }
    public ResponseObject Login(String payloadFromUI){
        ResponseObject response = new ResponseObject();
        JSONObject jsonPayload = new JSONObject(payloadFromUI);
        Account account;
        try {

            account = accountRepo.fetchAccountByEmail(jsonPayload.getString("email"));
            if(account != null && checkPassword(jsonPayload.getString("password"), account.getPassword())){
                response.setStatus(200);
                response.setMessage("You are now logged in");
                response.setData(account);
            }
            else{
                response.setStatus(403);
                response.setMessage("Email or Password was incorrect");
                response.setData("");
            }
        }
        catch (Exception e){
            response.setStatus(411);
            response.setMessage("An error occurred when creating your account");
            response.setData("An error occurred when creating an account :  " + e.getMessage());
        }
        return response;
    }
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        int workload = 12;
        String salt = BCrypt.gensalt(workload);
        String hashed_password = BCrypt.hashpw(password, salt);

        return(hashed_password);
    }
    private boolean checkPassword(String password_plaintext, String stored_hash) {
        boolean password_verified = false;

        if(null == stored_hash || !stored_hash.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

        password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

        return(password_verified);
    }

}
