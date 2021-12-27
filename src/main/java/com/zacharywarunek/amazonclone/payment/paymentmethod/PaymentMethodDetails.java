package com.zacharywarunek.amazonclone.payment.paymentmethod;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PaymentMethodDetails {
  private Long paymentTypeId;
  private String name;
  private String number;
  private String exp;
  private String cvv;
  private Long addressId;
}
