package com.zacharywarunek.amazonclone.config;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class Secure {
    protected final Log logger = LogFactory.getLog(getClass());
    private AccountRepo accountRepo;

    public boolean checkUserId(Authentication auth, Long account_id) {
        Optional<Account> result = accountRepo.findAccountByUsername(auth.getName());
        if(result.isPresent() && (result.get().getId().equals(account_id) ||
                auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))) {
            logger.info(result.get().getAuthorities() + "|" + result.get().getUsername() + "|" + result.get().getId() +
                                "\tAccessing id " + account_id);
            return true;
        }
        return false;


    }
}
