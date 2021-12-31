package com.zacharywarunek.amazonclone.product;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(path = "api/v1/products")
public class ProductController {

  ProductService productService;

  @GetMapping("/s")
  public void getAllBySpecification(
      @RequestParam(value = "sellers", required = false) List<Integer> sellerIds,
      @RequestParam(value = "priceFrom", required = false) Double priceFrom,
      @RequestParam(value = "priceTo", required = false) Double priceTo,
      @RequestParam(value = "prime", required = false) Boolean primeEligible,
      @RequestParam(value = "inStock", required = false) Boolean inStock,
      @RequestParam(value = "c", required = false) List<Integer> categoryIds,
      Pageable pageable
  ) {
    System.out.println("Test");
  }

  @GetMapping("/k")
  public void getAllBySpecification(
      ProductSearchCriteria productSearchCriteria,
      Pageable pageable
  ) {
    System.out.println("Test");
  }
}
