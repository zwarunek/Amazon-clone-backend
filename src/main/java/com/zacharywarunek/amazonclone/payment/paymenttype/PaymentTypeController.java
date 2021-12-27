package com.zacharywarunek.amazonclone.payment.paymenttype;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "api/v1/payment-types")
public class PaymentTypeController {
  PaymentTypeService paymentTypeService;
}
