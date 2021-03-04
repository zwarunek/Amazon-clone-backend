package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Account;
import com.zacharywarunek.kettering.cs461project.entitys.Category;
import com.zacharywarunek.kettering.cs461project.entitys.Product;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface ICategoryRepo extends IJPABaseRepo<Category, String> {

    @Query(value = "SELECT * FROM Category", nativeQuery = true)
    Collection<Category> fetchAllCategories();
}
