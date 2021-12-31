package com.zacharywarunek.amazonclone.product;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
@Getter
@Setter
public class ProductSearchCriteria implements Specification<Product> {
  private List<Long> sellers;
  private Double priceMin;
  private Double priceMax;
  private Boolean primeEligible;
  private Boolean inStock;
  private List<Long> categories;

  @Override
  public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
    List<Predicate> predicates = new ArrayList<>();

    if (sellers != null) {
      predicates.add((root.get("seller")).in(sellers));
    }
    if (priceMin != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get("price"), priceMin));
    }
    if (priceMax != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get("price"), priceMax));
    }
    if (primeEligible != null) {
      predicates.add(cb.equal(root.get("primeEligible"), primeEligible));
    }
    if (inStock != null) {
      predicates.add(
          inStock ? cb.greaterThanOrEqualTo(root.get("stock"), 1) : cb.equal(root.get("stock"), 0));
    }
    if (categories != null) {
      predicates.add((root.get("category")).in(categories));
    }

    return cb.and(predicates.toArray(new Predicate[] {}));
  }
}
