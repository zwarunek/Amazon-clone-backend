package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.PaymentMethod;
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
