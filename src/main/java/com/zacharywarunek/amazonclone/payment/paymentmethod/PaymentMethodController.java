package com.zacharywarunek.amazonclone.payment.paymentmethod;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "api/v1/accounts/{account_id}/payment-methods")
public class PaymentMethodController {
  PaymentMethodService paymentMethodService;
}
