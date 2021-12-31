package com.zacharywarunek.amazonclone.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.zacharywarunek.amazonclone.category.Category;
import com.zacharywarunek.amazonclone.category.CategoryRepo;
import com.zacharywarunek.amazonclone.seller.Seller;
import com.zacharywarunek.amazonclone.seller.SellerRepo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductRepoTest {

  @Autowired private ProductRepo productRepo;
  @Autowired private CategoryRepo categoryRepo;
  @Autowired private SellerRepo sellerRepo;
  private List<Product> products;
  private List<Category> categories;
  private List<Seller> sellers;

  @BeforeEach
  void setup() {
    categories = new ArrayList<>(Arrays.asList(new Category("first"), new Category("second")));
    categoryRepo.saveAll(categories);
    sellers = new ArrayList<>(Arrays.asList(new Seller("first"), new Seller("second")));
    sellerRepo.saveAll(sellers);
    products =
        new ArrayList<>(
            Arrays.asList(
                new Product("bname1", "desc1", sellers.get(0), 1, true, 5, categories.get(0)),
                new Product("aname2", "desc2", sellers.get(0), 2, false, 4, categories.get(1)),
                new Product("name3", "desc3", sellers.get(1), 6, true, 4, categories.get(0)),
                new Product("name4", "desc4", sellers.get(1), 5, false, 4, categories.get(0)),
                new Product("name5", "desc5", sellers.get(1), 3, true, 4, categories.get(1))));
    productRepo.saveAll(products);
  }

  @AfterEach
  void tearDown() {
    productRepo.deleteAll();
  }

  @Test
  void findAllByPrimeEligible() {
    assertThat(productRepo.findAll(new ProductSearchCriteria(null, null, null, true, null, null)))
        .isEqualTo(
            new ArrayList<>(Arrays.asList(products.get(0), products.get(2), products.get(4))));
    assertThat(productRepo.findAll(new ProductSearchCriteria(null, null, null, false, null, null)))
        .isEqualTo(new ArrayList<>(Arrays.asList(products.get(1), products.get(3))));
  }

  @Test
  void findAllBySeller() {
    assertThat(productRepo.findAll(new ProductSearchCriteria(Arrays.asList(1L), null, null, null, null, null)))
        .isEqualTo(
            new ArrayList<>(Arrays.asList(products.get(0), products.get(1))));
    assertThat(productRepo.findAll(new ProductSearchCriteria(Arrays.asList(2L), null, null, null, null, null)))
        .isEqualTo(
            new ArrayList<>(Arrays.asList(products.get(2), products.get(3), products.get(4))));
  }
}
