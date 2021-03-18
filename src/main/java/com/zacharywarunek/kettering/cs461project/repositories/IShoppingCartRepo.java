package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.ShoppingCart;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;

public interface IShoppingCartRepo extends IJPABaseRepo<ShoppingCart, String> {
    @Query(value = "SELECT * FROM ShoppingCart", nativeQuery = true)
    Collection<ShoppingCart> fetchAllShoppingCart();

    @Query(value = "SELECT * FROM ShoppingCart WHERE AccountID=?", nativeQuery = true)
    ShoppingCart fetchShoppingCartByAccountID(int accountID);

    @Query(value = "SELECT * FROM ShoppingCart WHERE ShoppingCartID=?", nativeQuery = true)
    ShoppingCart fetchShoppingCartByShoppingCartID(int shoppingCartID);

    @Query(value = "SELECT * FROM ShoppingCart WHERE ShoppingCart=? AND AccountID=?", nativeQuery = true)
    ShoppingCart fetchShoppingCartByShoppingCartAndAccountID(int accountID, int shoppingCart);

    @Query(value = "SELECT * FROM ShoppingCart WHERE PrimeEligible=? AND AccountID=?", nativeQuery = true)
    ShoppingCart fetchShoppingCartByPrimeEligibleAndAccountID(int accountID, boolean primeEligible);

    @Query(value = "SELECT * FROM ShoppingCart WHERE ProductListID=? AND AccountID=?", nativeQuery = true)
    ShoppingCart fetchShoppingCartByProductListIDAndAccountID(int accountID, int productListID);

    @Query(value = "DELETE * FROM ShoppingCart WHERE ProductListID=? AND AccountID=?", nativeQuery = true)
    ShoppingCart deleteByPIDaAID(int accountID, int productListID);
}