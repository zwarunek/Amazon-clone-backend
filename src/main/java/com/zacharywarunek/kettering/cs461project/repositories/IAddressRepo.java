package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Address;
import com.zacharywarunek.kettering.cs461project.entitys.PaymentMethod;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IAddressRepo extends IJPABaseRepo<Address, String> {

    @Query(value = "SELECT * FROM Address where AccountId = ? order by Favorite DESC", nativeQuery = true)
    Collection<Address> fetchAllAddressesByAccountId(int accountId);


    @Query(value = "delete FROM Address where AddressId = ?", nativeQuery = true)
    @Modifying
    void deleteAddressById(int addressId);
}
