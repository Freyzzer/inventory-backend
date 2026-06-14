package com.inventario.inventory_backend.category;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByCompanyIdAndActiveTrueOrderByNameAsc(Long companyId);

    Optional<Category> findByIdAndCompanyIdAndActiveTrue(Long id, Long companyId);

    boolean existsByNameIgnoreCaseAndCompanyId(String name, Long companyId);

    boolean existsByNameIgnoreCaseAndCompanyIdAndIdNot(String name, Long companyId, Long id);

    boolean existsByIdAndCompanyIdAndActiveTrue(Long id, Long companyId);
}
