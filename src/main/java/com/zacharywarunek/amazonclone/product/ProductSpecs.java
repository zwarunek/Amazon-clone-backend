package com.zacharywarunek.amazonclone.product;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {
  public static Specification<Product> findByCriteria(ProductSearchCriteria searchCriteria) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (searchCriteria.getSellers() != null && !searchCriteria.getSellers().isEmpty()) {
        predicates.add(cb.equal(root.get("seller"), searchCriteria.getSellers()));
      }
      if (searchCriteria.getPriceMin() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), searchCriteria.getPriceMin()));
      }
      if (searchCriteria.getPriceMax() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("price"), searchCriteria.getPriceMax()));
      }
      if (searchCriteria.getPrimeEligible() != null) {
        predicates.add(cb.equal(root.get("primeEligible"), searchCriteria.getPrimeEligible()));
      }
      if (searchCriteria.getInStock() != null) {
        predicates.add(
            searchCriteria.getInStock()
                ? cb.greaterThanOrEqualTo(root.get("stock"), 1)
                : cb.equal(root.get("stock"), 0));
      }
      if (searchCriteria.getCategories() != null && !searchCriteria.getCategories().isEmpty()) {
        predicates.add(cb.equal(root.get("category"), searchCriteria.getCategories()));
      }

      return cb.and(predicates.toArray(new Predicate[] {}));
    };
  }
}
