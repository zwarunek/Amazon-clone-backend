package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Account;
import com.zacharywarunek.kettering.cs461project.entitys.Product;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface IProductRepo extends IJPABaseRepo<Product, String> {

    @Query(value = "SELECT * FROM Product", nativeQuery = true)
    Collection<Product> fetchAllProducts();

    @Query(value = "SELECT * FROM Product WHERE PID=?", nativeQuery = true)
    Product fetchProductById(int productId);


    @Query(value = "SELECT * FROM Product " +
            "where Product.Name LIKE :search OR Product.Description LIKE :search", nativeQuery = true)
    Collection<Product> searchProducts(@Param("search") String search);
}
