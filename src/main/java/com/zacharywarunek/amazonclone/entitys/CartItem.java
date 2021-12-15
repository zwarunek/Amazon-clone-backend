package com.zacharywarunek.amazonclone.entitys;

import javax.persistence.*;

@Entity
@Table(name = "CartItem")
public class CartItem {

    //create table ShoppingCart
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CartItemID")
    private int cartItemId;

    @Column(name = "AccountID")
    private int accountID;

    @Column(name = "PID")
    private int productId;

    @Column(name = "Quantity")
    private int quantity;

    @Column(name = "Price")
    private double price;


    public CartItem constructEntity(int accountID, double price, int quantity, int productId) {
        CartItem cartItem = new CartItem();
        cartItem.setAccountID(accountID);
        cartItem.setPrice(price);
        cartItem.setQuantity(quantity);
        cartItem.setProductId(productId);
        return cartItem;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int isQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }
}
