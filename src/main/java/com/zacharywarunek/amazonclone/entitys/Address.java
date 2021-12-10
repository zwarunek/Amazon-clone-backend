package com.zacharywarunek.amazonclone.entitys;

import javax.persistence.*;

@Entity
@Table(name = "Address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressId")
    private int addressId;
    @Column(name = "AccountId")
    private int accountId;
    @Column(name = "Address")
    private String address;
    @Column(name = "City")
    private String city;
    @Column(name = "State")
    private String state;
    @Column(name = "Zipcode")
    private int zipcode;
    @Column(name = "Favorite")
    private boolean favorite;
    @Column(name = "Name")
    private String name;

    public Address constructEntity(int accountId, String address, String city, String state, int zipcode, String name) {
        Address paymentMethod = new Address();
        paymentMethod.setAccountId(accountId);
        paymentMethod.setAddress(address);
        paymentMethod.setCity(city);
        paymentMethod.setState(state);
        paymentMethod.setZipcode(zipcode);
        paymentMethod.setName(name);
        return paymentMethod;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int pmId) {
        this.addressId = pmId;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String street) {
        this.address = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
