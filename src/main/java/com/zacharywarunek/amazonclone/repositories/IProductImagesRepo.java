package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.entitys.ProductImages;
import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IProductImagesRepo extends IJPABaseRepo<ProductImages> {

    @Query(value = "SELECT src FROM product_images WHERE product_id=?", nativeQuery = true)
    Collection<String> fetchProductImagesById(int product_id);
}
