package com.zacharywarunek.amazonclone.payment.paymentmethod;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.address.Address;
import com.zacharywarunek.amazonclone.payment.paymenttype.PaymentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payment_method")
public class PaymentMethod {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;

  @ManyToOne
  @JoinColumn(name = "type_id")
  private PaymentType paymentType;

  private String name;
  private String number;
  private String exp;
  private String cvv;
  private Boolean favorite;

  @ManyToOne
  @JoinColumn(name = "address_id")
  private Address address;

  public PaymentMethod(
      Account account,
      PaymentType paymentType,
      String name,
      String number,
      String exp,
      String cvv,
      Boolean favorite,
      Address address) {
    this.account = account;
    this.paymentType = paymentType;
    this.name = name;
    this.number = number;
    this.exp = exp;
    this.cvv = cvv;
    this.favorite = favorite;
    this.address = address;
  }
}
