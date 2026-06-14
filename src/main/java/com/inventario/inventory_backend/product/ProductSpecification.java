package com.inventario.inventory_backend.product;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

final class ProductSpecification {

    private ProductSpecification() {
    }

    static Specification<Product> withFilters(String name, String sku, Long categoryId,
                                               BigDecimal minPrice, BigDecimal maxPrice, Boolean active,
                                               Long companyId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (sku != null) {
                predicates.add(cb.like(cb.lower(root.get("sku")), "%" + sku.toLowerCase() + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("unitPrice"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("unitPrice"), maxPrice));
            }
            predicates.add(cb.equal(root.get("active"), active != null ? active : true));
            predicates.add(cb.equal(root.get("company").get("id"), companyId));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
