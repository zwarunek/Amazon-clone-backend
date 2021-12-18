package com.zacharywarunek.amazonclone.address;

import com.zacharywarunek.amazonclone.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

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
    private String city;
    private String state;
    private int zipcode;
    private boolean favorite;
    private String first_name;
    private String last_name;

    public Address(Account account, String city, String state, int zipcode, boolean favorite, String first_name,
                   String last_name) {

        this.account = account;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
        this.favorite = favorite;
        this.first_name = first_name;
        this.last_name = last_name;
    }
    @Override
    public String toString() {
        return String.format(
                "Address [id=%s, city=%s, state=%s, zipcode=%s, favorite=%s, first_name=%s, last_name=%s]",
                id, city, state, zipcode, favorite, first_name, last_name);
    }

}
