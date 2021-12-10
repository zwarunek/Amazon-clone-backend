package com.zacharywarunek.amazonclone.service;

import com.zacharywarunek.amazonclone.entitys.Account;
import com.zacharywarunek.amazonclone.repositories.IAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private IAccountRepo accountRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepo.fetchAccountByEmail(username);
        return new User(account.getEmail(), account.getPassword(), new ArrayList<>());
    }
}
