package com.zacharywarunek.amazonclone.entitys;

import javax.persistence.*;

@Entity
@Table(name = "Product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PID")
    private int productId;
    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;
    @Column(name = "Seller")
    private String seller;
    @Column(name = "Price")
    private double price;
    @Column(name = "PrimeEligible")
    private boolean primeEligible;
    @Column(name = "Stock")
    private int stock;
    @Column(name = "Category")
    private int category;

    public Product constructEntity(String name, String description, String seller, double price, boolean primeEligible,
                                   int stock, int category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setSeller(seller);
        product.setPrice(price);
        product.setPrimeEligible(primeEligible);
        product.setStock(stock);
        product.setCategory(category);
        return product;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean getPrimeEligible() {
        return primeEligible;
    }

    public void setPrimeEligible(boolean primeEligible) {
        this.primeEligible = primeEligible;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
