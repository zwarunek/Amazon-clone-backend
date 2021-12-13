package com.zacharywarunek.amazonclone.repositories;

import com.zacharywarunek.amazonclone.repositories.JPA.IJPABaseRepo;
import com.zacharywarunek.amazonclone.entitys.PaymentType;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface IPaymentTypeRepo extends IJPABaseRepo<PaymentType> {

    @Query(value = "SELECT * FROM payment_type", nativeQuery = true)
    Collection<PaymentType> fetchAllPaymentTypes();
}
