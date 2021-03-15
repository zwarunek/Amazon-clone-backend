package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.PaymentMethod;
import com.zacharywarunek.kettering.cs461project.entitys.PaymentType;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IPaymentTypeRepo extends IJPABaseRepo<PaymentType, String> {

    @Query(value = "SELECT * FROM PaymentType", nativeQuery = true)
    Collection<PaymentType> fetchAllPaymentTypes();
}
