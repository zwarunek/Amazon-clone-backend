package com.zacharywarunek.amazonclone.product;

import com.zacharywarunek.amazonclone.util.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends IJPABaseRepo<Product>, JpaSpecificationExecutor<Product> {}
