package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.entitys.CartItem;
import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ICartItemRepo extends IJPABaseRepo<CartItem> {

    @Query(value = "SELECT * FROM cart_item WHERE account_id=?", nativeQuery = true)
    Collection<CartItem> fetchCartItemsByAccountID(int account_id);

    @Modifying
    @Query(value = "UPDATE cart_item SET quantity=? WHERE id=? ", nativeQuery = true)
    void changeQuantity(int quantity, int id);
}
