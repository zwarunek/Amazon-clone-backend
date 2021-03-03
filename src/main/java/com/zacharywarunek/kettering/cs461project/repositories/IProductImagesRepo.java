package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Account;
import com.zacharywarunek.kettering.cs461project.entitys.Product;
import com.zacharywarunek.kettering.cs461project.entitys.ProductImages;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IProductImagesRepo extends IJPABaseRepo<ProductImages, String> {

    @Query(value = "SELECT Image FROM ProductImages WHERE PID=?", nativeQuery = true)
    Collection<String> fetchProductImagesById(int productId);
}
