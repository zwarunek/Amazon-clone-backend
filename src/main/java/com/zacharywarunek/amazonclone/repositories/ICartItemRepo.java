package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.CartItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ICartItemRepo extends IJPABaseRepo<CartItem> {

    @Query(value = "SELECT * FROM CartItem WHERE AccountID=?", nativeQuery = true)
    Collection<CartItem> fetchCartItemsByAccountID(int accountID);

    @Modifying
    @Query(value = "UPDATE CartItem SET Quantity=? WHERE CartItemID=? ", nativeQuery = true)
    void changeQuantity( int newQuantity, int shoppingCartID);
}
