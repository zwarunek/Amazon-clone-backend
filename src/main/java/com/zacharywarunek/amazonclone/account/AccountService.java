package com.zacharywarunek.amazonclone.account;

import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.ACCOUNT_ID_NOT_FOUND;
import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.NULL_VALUES;
import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.USERNAME_NOT_FOUND;
import static com.zacharywarunek.amazonclone.exceptions.ExceptionResponses.USERNAME_TAKEN;

import com.zacharywarunek.amazonclone.address.AddressRepo;
import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.exceptions.BadRequestException;
import com.zacharywarunek.amazonclone.exceptions.EntityNotFoundException;
import com.zacharywarunek.amazonclone.exceptions.ExceptionResponses;
import com.zacharywarunek.amazonclone.exceptions.UnauthorizedException;
import com.zacharywarunek.amazonclone.exceptions.UsernameTakenException;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationToken;
import com.zacharywarunek.amazonclone.registration.token.ConfirmationTokenService;
import com.zacharywarunek.amazonclone.util.AuthRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AccountService implements UserDetailsService {

  protected final Log logger = LogFactory.getLog(getClass());
  private final PasswordEncoder passwordEncoder;
  private final ConfirmationTokenService confirmationTokenService;
  AccountRepo accountRepo;
  AddressRepo addressRepo;
  JwtUtil jwtUtil;

  public Account findById(Long account_id) throws EntityNotFoundException {
    return accountRepo
        .findById(account_id)
        .orElseThrow(
            () ->
                new EntityNotFoundException(String.format(ACCOUNT_ID_NOT_FOUND.label, account_id)));
  }

  public List<Account> getAll() {
    return accountRepo.findAll();
  }

  public String create(Account account) throws BadRequestException, UsernameTakenException {
    if (account.getPassword() == null
        || account.getUsername() == null
        || account.getLast_name() == null
        || account.getFirst_name() == null)
      throw new BadRequestException(ExceptionResponses.NULL_VALUES.label);
    if (accountRepo.checkIfUsernameExists(account.getUsername()))
      throw new UsernameTakenException(
          String.format(ExceptionResponses.USERNAME_TAKEN.label, account.getUsername()));

    account.setPassword(passwordEncoder.encode(account.getPassword()));
    accountRepo.save(account);
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken =
        new ConfirmationToken(
            token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), account);

    confirmationTokenService.saveConfirmationToken(confirmationToken);

    return token;
  }

  public String authenticate(AuthRequest authRequest)
      throws BadRequestException, UnauthorizedException {
    if (authRequest.getUsername() == null || authRequest.getPassword() == null)
      throw new BadRequestException(NULL_VALUES.label);
    Optional<Account> accountOptional =
        accountRepo.findAccountByUsername(authRequest.getUsername());
    if (!accountOptional.isPresent()
        || !passwordEncoder.matches(authRequest.getPassword(), accountOptional.get().getPassword()))
      throw new UnauthorizedException("Unauthorized");
    return jwtUtil.generateToken(accountOptional.get());
  }

  @Transactional
  public Account update(Long account_id, AccountDetails accountDetails)
      throws EntityNotFoundException, UsernameTakenException {
    Account account = findById(account_id);
    if (accountDetails.getUsername() != null
        && !accountDetails.getUsername().equals(account.getUsername()))
      if (accountRepo.findAccountByUsername(accountDetails.getUsername()).isPresent())
        throw new UsernameTakenException(
            String.format(USERNAME_TAKEN.label, accountDetails.getUsername()));
      else account.setUsername(accountDetails.getUsername());
    if (accountDetails.getPassword() != null)
      account.setPassword(passwordEncoder.encode(accountDetails.getPassword()));
    if (accountDetails.getFirst_name() != null
        && !accountDetails.getFirst_name().equals(account.getFirst_name()))
      account.setFirst_name(accountDetails.getFirst_name());
    if (accountDetails.getFirst_name() != null
        && !accountDetails.getLast_name().equals(account.getLast_name()))
      account.setLast_name(accountDetails.getLast_name());
    return account;
  }

  public void delete(Long account_id) throws EntityNotFoundException {
    Account account = findById(account_id);
    confirmationTokenService.deleteAllAtAccount(account);
    addressRepo.deleteAllAtAccount(account);
    accountRepo.deleteById(account.getId());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account =
        accountRepo
            .findAccountByUsername(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        String.format(USERNAME_NOT_FOUND.name(), username)));
    return new User(account.getUsername(), account.getPassword(), account.getAuthorities());
  }

  public void enable(String username) {
    accountRepo.enableAccount(username);
  }
}
