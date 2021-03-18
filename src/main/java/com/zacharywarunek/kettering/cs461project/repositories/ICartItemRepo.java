package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.CartItem;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ICartItemRepo extends IJPABaseRepo<CartItem, String> {

    @Query(value = "SELECT * FROM CartItem WHERE AccountID=?", nativeQuery = true)
    Collection<CartItem> fetchCartItemsByAccountID(int accountID);

    @Modifying
    @Query(value = "UPDATE CartItem SET Quantity=? WHERE CartItemID=? ", nativeQuery = true)
    void changeQuantity( int newQuantity, int shoppingCartID);
}
