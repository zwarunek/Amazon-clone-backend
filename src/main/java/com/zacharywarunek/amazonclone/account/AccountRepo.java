package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AccountRepo extends IJPABaseRepo<Account> {

    @Query(value = "SELECT * FROM Account WHERE username=?", nativeQuery = true)
    Optional<Account> findAccountByUsername(String username);

    @Query(value = "SELECT CAST(IIF(EXISTS(SELECT * FROM Account where username like ?), 1, 0)AS BIT)",
           nativeQuery = true)
    boolean checkIfUsernameExists(String username);

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Account a " + "SET a.enabled = TRUE WHERE a.username = ?1")
    int enableAccount(String username);
}
