package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.Category;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ICategoryRepo extends IJPABaseRepo<Category> {

    @Query(value = "SELECT * FROM category", nativeQuery = true)
    Collection<Category> fetchAllCategories();
}
