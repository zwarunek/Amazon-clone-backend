package com.zacharywarunek.amazonclone.registration.token;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.util.JPA.IJPABaseRepo;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface ConfirmationTokenRepo extends IJPABaseRepo<ConfirmationToken> {

  Optional<ConfirmationToken> findByToken(String token);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE ConfirmationToken c " + "SET c.confirmed_at = ?2 " + "WHERE c.token = ?1")
  void updateConfirmedAt(String token, LocalDateTime confirmedAt);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM ConfirmationToken WHERE account = ?1")
  int deleteAllByAccountId(Account account);
}
