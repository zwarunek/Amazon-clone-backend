package com.zacharywarunek.amazonclone.product;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductDetails {

  private String name;
  private String description;
  private String seller;
  private BigDecimal price;
  private boolean primeEligible;
  private Integer stock;
  private Integer categoryId;
}
