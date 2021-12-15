package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.entitys.Address;
import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IAddressRepo extends IJPABaseRepo<Address> {

    @Query(value = "SELECT * FROM address where account_id = ? order by favorite DESC", nativeQuery = true)
    Collection<Address> fetchAllAddressesByAccountId(int account_id);


    @Query(value = "delete FROM address where id = ?", nativeQuery = true)
    @Modifying
    void deleteAddressById(int id);
}
