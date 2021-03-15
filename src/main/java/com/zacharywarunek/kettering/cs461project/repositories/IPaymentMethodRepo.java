package com.zacharywarunek.kettering.cs461project.repositories;

import com.zacharywarunek.kettering.cs461project.entitys.Category;
import com.zacharywarunek.kettering.cs461project.entitys.PaymentMethod;
import com.zacharywarunek.kettering.cs461project.repositories.JPA.IJPABaseRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IPaymentMethodRepo extends IJPABaseRepo<PaymentMethod, String> {

    @Query(value = "SELECT * FROM PaymentMethod where AccountId = ?", nativeQuery = true)
    Collection<PaymentMethod> fetchAllPaymentMethods(int accountId);


    @Modifying
    @Query(value = "delete FROM PaymentMethod where PMID = ?", nativeQuery = true)
    void deletePaymentMethodById(int pmid);
}
