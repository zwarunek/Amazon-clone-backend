package com.zacharywarunek.amazonclone.payment.paymentmethod;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.util.JPA.IJPABaseRepo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentMethodRepo extends IJPABaseRepo<PaymentMethod> {

  @Query(value = "SELECT pm FROM PaymentMethod pm WHERE pm.account = :account")
  List<PaymentMethod> findPaymentMethodByByAccount(Account account);

  @Query(value = "SELECT pm FROM PaymentMethod pm WHERE pm.account = :account AND pm.favorite = TRUE")
  Optional<PaymentMethod> findFavoritePaymentMethodByAccount(Account account);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM PaymentMethod pm WHERE pm.account = :account")
  int deleteAllAtAccount(Account account);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE PaymentMethod pm SET pm.favorite = TRUE WHERE pm.id = :id")
  int setFavorite(Long id);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE PaymentMethod pm SET pm.favorite = FALSE WHERE pm.account = :account")
  int resetFavorite(Account account);
}
