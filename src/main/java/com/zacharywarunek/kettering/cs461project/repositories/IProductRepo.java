package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Account;
import com.zacharywarunek.kettering.cs461project.entitys.Product;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface IProductRepo extends IJPABaseRepo<Product, String> {

    @Query(value = "SELECT * FROM Product", nativeQuery = true)
    Collection<Product> fetchAllProducts();

    @Query(value = "SELECT * FROM Product WHERE PID=?", nativeQuery = true)
    Product fetchProductById(int productId);
}
