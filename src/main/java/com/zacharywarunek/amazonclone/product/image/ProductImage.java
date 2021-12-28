package com.zacharywarunek.amazonclone.product.image;

import com.zacharywarunek.amazonclone.product.Product;
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
@Table(name = "product_image")
public class ProductImage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "product_id")
  private Product product;

  @Lob
  @Column(name = "src", nullable = false)
  private String src;

  @Column(name = "placement")
  private Integer placement;

  public ProductImage(Product product, String src, Integer placement) {
    this.product = product;
    this.src = src;
    this.placement = placement;
  }

  public Integer getPlacement() {
    return placement;
  }

  public void setPlacement(Integer placement) {
    this.placement = placement;
  }

  public String getSrc() {
    return src;
  }

  public void setSrc(String src) {
    this.src = src;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}
