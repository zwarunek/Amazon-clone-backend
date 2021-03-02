package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Account;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

public interface IAccountRepo extends IJPABaseRepo<Account, String> {
    @Query(value = "SELECT * FROM Account", nativeQuery = true)
    Collection<Account> fetchAllAccounts();

    @Query(value = "SELECT * FROM [Account] WHERE [Email]=?", nativeQuery = true)
    Account fetchAccountByEmail(String email);

}
