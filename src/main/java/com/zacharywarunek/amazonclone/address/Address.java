package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.Account;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Address implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;

  private String address;
  private String city;
  private String state;
  private Integer zipcode;
  private Boolean favorite;
  private String first_name;
  private String last_name;

  public Address(
      Account account,
      String address,
      String city,
      String state,
      Integer zipcode,
      Boolean favorite,
      String first_name,
      String last_name) {

    this.account = account;
    this.address = address;
    this.city = city;
    this.state = state;
    this.zipcode = zipcode;
    this.favorite = favorite;
    this.first_name = first_name;
    this.last_name = last_name;
  }
}
