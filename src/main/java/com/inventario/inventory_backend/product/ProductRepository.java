package com.inventario.inventory_backend.product;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	List<Product> findAllByCompanyIdAndActiveTrueOrderByNameAsc(Long companyId);

	List<Product> findAllByCompanyIdAndCategoryIdAndActiveTrueOrderByNameAsc(Long companyId, Long categoryId);

	Optional<Product> findByIdAndCompanyIdAndActiveTrue(Long id, Long companyId);

	Optional<Product> findBySkuIgnoreCaseAndCompanyId(String sku, Long companyId);

	boolean existsBySkuIgnoreCaseAndCompanyId(String sku, Long companyId);

	boolean existsBySkuIgnoreCaseAndCompanyIdAndIdNot(String sku, Long companyId, Long id);

	boolean existsByCategoryIdAndCompanyIdAndActiveTrue(Long categoryId, Long companyId);
}
