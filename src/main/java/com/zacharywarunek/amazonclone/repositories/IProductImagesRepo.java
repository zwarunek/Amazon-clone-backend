package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.ProductImages;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IProductImagesRepo extends IJPABaseRepo<ProductImages> {

    @Query(value = "SELECT Image FROM ProductImages WHERE PID=?", nativeQuery = true)
    Collection<String> fetchProductImagesById(int productId);
}
