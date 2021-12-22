package com.zacharywarunek.amazonclone.payment.paymentmethod;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentMethodService {
    PaymentMethodRepo paymentMethodRepo;
}
