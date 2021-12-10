package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.entitys.Product;
import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IProductRepo extends IJPABaseRepo<Product> {

    @Query(value = "SELECT * FROM Product", nativeQuery = true)
    Collection<Product> fetchAllProducts();

    @Query(value = "SELECT * FROM Product WHERE PID=?", nativeQuery = true)
    Product fetchProductById(int productId);
}
