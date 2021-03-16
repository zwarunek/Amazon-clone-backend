package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Address;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;

public interface IAddressRepo extends IJPABaseRepo<Address, String> {
    @Query(value = "SELECT * FROM Address", nativeQuery = true)
    Collection<Address> fetchAllAddresses();

    @Query(value = "SELECT * FROM Address WHERE AccountID=?", nativeQuery = true)
    Address fetchAddressByAccountID(int accountID);

    @Query(value = "SELECT * FROM Address WHERE AddressID=?", nativeQuery = true)
    Address fetchAddressByAddressID(int addressID);

    @Query(value = "SELECT * FROM Address WHERE Street=? AND AccountID=?", nativeQuery = true)
    Address fetchAddressByStreetAndAccountID(int accountID, String street);

    @Query(value = "SELECT * FROM Address WHERE City=? AND AccountID=?", nativeQuery = true)
    Address fetchAddressByCityAndAccountID(int accountID, String city);

    @Query(value = "SELECT * FROM Address WHERE State=? AND AccountID=?", nativeQuery = true)
    Address fetchAddressByStateAndAccountID(int accountID, String state);

    @Query(value = "SELECT * FROM Address WHERE Zipcode=? AND AccountID=?", nativeQuery = true)
    Address fetchAddressByZipcodeAndAccountID(int accountID, int zipcode);

    @Query(value = "SELECT * FROM Address WHERE Country=? AND AccountID=?", nativeQuery = true)
    Address fetchAddressByCountryAndAccountID(int accountID, String country);

    @Modifying
    @Transactional
    @Query(value = "update Address set Favorite = ? where AccountID = ?", nativeQuery = true)
    void changeAddressFavorite(boolean Favorite, int accountId);

}
