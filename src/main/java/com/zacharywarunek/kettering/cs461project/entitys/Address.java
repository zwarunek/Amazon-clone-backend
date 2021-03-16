package com.zacharywarunek.kettering.cs461project.entitys;

import javax.persistence.*;

@Entity
@Table(name = "Address")
public class Address {

    //create table address
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "AccountID") private int accountID;

    @Column(name = "AddressID") private int addressID;

    @Column(name = "HouseNumber") private String houseNbr;

    @Column(name = "Street") private String street;

    @Column(name = "City") private  String city;

    @Column(name = "State") private  String state;

    @Column(name = "Country") private  String country;

    @Column(name = "Zipcode") private  int zipcode;

    @Column(name = "Favorite") private  boolean favorite;

    public Address constructEntity(int accountID, int addressID, String houseNbr, String street, String city, String state, String country, int zipcode, boolean favorite) {
        Address address = new Address();
        address.setAccountID(accountID);
        address.setAddressID(addressID);
        address.setHouseNbr(houseNbr);
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setZipcode(zipcode);
        address.setFavorite(favorite);
        return address;
    }

    @Override
    public String toString(){
        return String.format("Address [addressID=%d, addressID=%d, houseNbr=%s,street=%s,city=%s,state=%s,country=%s,zipcode=%d,favorite=%b]",addressID,addressID,houseNbr,street,city,state,country,zipcode,favorite);
    }
    public int getAccountID(){
        return accountID;
    }
    public void setAccountID(int accountID){
        this.accountID =accountID;
    }
    public int getAddressID(){
        return addressID;
    }
    public void setAddressID(int addressID){
        this.addressID =addressID;
    }
    public String getHouseNbr(){
        return houseNbr;
    }
    public void setHouseNbr(String houseNbr){
        this.houseNbr = houseNbr;
    }
    public String getStreet(){
        return street;
    }
    public void setStreet(String street){
        this.street = street;
    }
    public String getCity(){
        return city;
    }
    public void setCity(String city){
        this.city = city;
    }
    public String getState(){
        return state;
    }
    public void setState(String state){
        this.state = state;
    }
    public String getCountry(){
        return country;
    }
    public void setCountry(String country){
        this.country = country;
    }
    public int getZipcode(){
        return zipcode;
    }
    public void setZipcode(int zipcode){
        this.zipcode = zipcode;
    }
    public boolean getFavorite(){
        return favorite;
    }
    public void setFavorite(boolean favorite){
        this.favorite = favorite;
    }



}
