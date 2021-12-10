package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.Address;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IAddressRepo extends IJPABaseRepo<Address> {

    @Query(value = "SELECT * FROM Address where AccountId = ? order by Favorite DESC", nativeQuery = true)
    Collection<Address> fetchAllAddressesByAccountId(int accountId);


    @Query(value = "delete FROM Address where AddressId = ?", nativeQuery = true)
    @Modifying
    void deleteAddressById(int addressId);
}
