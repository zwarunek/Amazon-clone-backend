package com.zacharywarunek.amazonclone.config;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.address.Address;
import com.zacharywarunek.amazonclone.address.AddressRepo;
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
  private AddressRepo addressRepo;

  public boolean checkAccountIdAuth(Authentication auth, Long account_id) {
    Optional<Account> result = accountRepo.findAccountByUsername(auth.getName());
    if (result.isPresent() && result.get().getId().equals(account_id)
        || auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
      logger.info("\t\tAccessing account_id " + account_id);
      return true;
    }
    return false;
  }

  public boolean checkAddressIdAuth(Authentication auth, Long account_id, Long address_id) {
    Optional<Address> optionalAddress = addressRepo.findById(address_id);
    if (checkAccountIdAuth(auth, account_id)
        && ((optionalAddress.isPresent()
                && optionalAddress.get().getAccount().getUsername().equals(auth.getName()))
            || auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))) {
      logger.info("\t\tAccessing address_id " + address_id);
      return true;
    }
    return false;
  }
}
