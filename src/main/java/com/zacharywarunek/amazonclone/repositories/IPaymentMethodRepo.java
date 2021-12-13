package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.PaymentMethod;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IPaymentMethodRepo extends IJPABaseRepo<PaymentMethod> {

    @Query(value = "SELECT * FROM payment_method where account_id = ?", nativeQuery = true)
    Collection<PaymentMethod> fetchAllPaymentMethods(int account_id);


    @Modifying
    @Query(value = "delete FROM payment_method where id = ?", nativeQuery = true)
    void deletePaymentMethodById(int id);
}
