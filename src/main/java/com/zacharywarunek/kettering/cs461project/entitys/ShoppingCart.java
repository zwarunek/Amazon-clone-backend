package com.zacharywarunek.kettering.cs461project.entitys;

import javax.persistence.*;
@Entity
@Table(name = "ShoppingCart")
public class ShoppingCart{

    //create table ShoppingCart
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "AccountID") private int accountID;

    @Column(name = "Total") private double total;

    @Column(name = "PrimeEligible") private boolean primeEligible;

    @Column(name = "ProductListID") private  int productListID;


    public ShoppingCart constructEntity(int accountID, double total, boolean primeEligible, int productListID) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setAccountID(accountID);
        shoppingCart.setTotal(total);
        shoppingCart.setPrimeEligible(primeEligible);
        shoppingCart.setProductListID(productListID);
        return shoppingCart;
    }

    @Override
    public String toString(){
        return String.format("ShoppingCart [accountID=%d, shoppingCartID=%d, total=%d, primeEligible=%b, productListID=%d]",accountID, shoppingCartID, total, primeEligible, productListID);
    }
    public int getAccountID(){
        return accountID;
    }
    public void setAccountID(int accountID){
        this.accountID =accountID;
    }
    public double getTotal(){
        return total;
    }
    public void setTotal(double total){
        this.total = total;
    }
    public boolean getPrimeEligible(){
        return primeEligible;
    }
    public void setPrimeEligible(boolean primeEligible){
        this.primeEligible = primeEligible;
    }
    public int getProductList(){
        return productListID;
    }
    public void setProductListID(int productListID){
        this.productListID = productListID;
    }
}
