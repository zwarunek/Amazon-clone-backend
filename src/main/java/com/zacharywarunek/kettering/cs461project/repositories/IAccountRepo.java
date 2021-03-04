package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Account;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IAccountRepo extends IJPABaseRepo<Account, String> {
    @Query(value = "SELECT * FROM Account", nativeQuery = true)
    Collection<Account> fetchAllAccounts();

    @Query(value = "SELECT * FROM Account WHERE Email=?", nativeQuery = true)
    Account fetchAccountByEmail(String email);

    @Query(value = "SELECT CAST(" +
            "               CASE WHEN EXISTS(SELECT * FROM Account where Account.Email like ?) THEN 1" +
            "                    ELSE 0" +
            "                   END" +
            "           AS BIT)", nativeQuery = true)
    boolean checkIfExists(String email);

}
