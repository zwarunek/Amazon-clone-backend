package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.util.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AddressRepo extends IJPABaseRepo<Address> {

  @Query(value = "SELECT a FROM Address a WHERE a.account = :account")
  List<Address> findAddressByAccount(Account account);

  @Query(value = "SELECT a FROM Address a WHERE a.account = :account AND a.favorite = TRUE")
  Optional<Address> findFavoriteAddressByAccount(Account account);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("DELETE FROM Address a WHERE a.account = :account")
  int deleteAllAtAccount(Account account);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE Address a SET a.favorite = TRUE WHERE a.id = :id")
  int setFavorite(Long id);

  @Transactional
  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE Address a SET a.favorite = FALSE WHERE a.account = :account")
  int resetFavorite(Account account);
}
