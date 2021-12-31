package com.zacharywarunek.amazonclone.product;

import com.zacharywarunek.amazonclone.category.Category;
import com.zacharywarunek.amazonclone.seller.Seller;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Lob
  @Column(name = "description", nullable = false)
  private String description;

  @ManyToOne
  @JoinColumn(name = "seller_id")
  private Seller seller;

  @Column(name = "price", nullable = false, precision = 18, scale = 2)
  private BigDecimal price;

  @Column(name = "prime_eligible", nullable = false)
  private Boolean primeEligible = false;

  @Column(name = "stock", nullable = false)
  private Integer stock;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public Boolean getPrimeEligible() {
    return primeEligible;
  }

  public void setPrimeEligible(Boolean primeEligible) {
    this.primeEligible = primeEligible;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Seller getSeller() {
    return seller;
  }

  public void setSeller(Seller seller) {
    this.seller = seller;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Product(String name, String description, Seller seller, double price,
      Boolean primeEligible, Integer stock, Category category) {
    this.name = name;
    this.description = description;
    this.seller = seller;
    this.price = BigDecimal.valueOf(price);
    this.primeEligible = primeEligible;
    this.stock = stock;
    this.category = category;
  }
}
