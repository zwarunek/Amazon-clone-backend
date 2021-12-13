package com.zacharywarunek.amazonclone.account;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AccountRepo extends IJPABaseRepo<Account> {

    @Query(value = "SELECT * FROM Account WHERE username=?", nativeQuery = true)
    Optional<Account> findAccountByEmail(String email);

    @Query(value = "SELECT CAST(" +
            "               CASE WHEN EXISTS(SELECT * FROM Account where username like ?) THEN 1" +
            "                    ELSE 0" +
            "                   END" +
            "           AS BIT)", nativeQuery = true)
    boolean checkIfEmailExists(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Account a " +
            "SET a.enabled = TRUE WHERE a.username = ?1")
    void enableAccount(String email);
}
