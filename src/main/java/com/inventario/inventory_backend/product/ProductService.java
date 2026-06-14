package com.inventario.inventory_backend.product;

import com.inventario.inventory_backend.category.CategoryRepository;
import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.exception.DuplicateResourceException;
import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import com.inventario.inventory_backend.user.User;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> findAll(User currentUser) {
		return productRepository.findAllByCompanyIdAndActiveTrueOrderByNameAsc(currentUser.getCompany().getId())
				.stream()
				.map(ProductMapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public Page<ProductResponse> search(String name, String sku, Long categoryId,
                                         BigDecimal minPrice, BigDecimal maxPrice, Boolean active, Pageable pageable,
                                         User currentUser) {
		return productRepository.findAll(
						ProductSpecification.withFilters(name, sku, categoryId, minPrice, maxPrice, active,
                                currentUser.getCompany().getId()),
						pageable)
				.map(ProductMapper::toResponse);
	}

	@Transactional(readOnly = true)
	public ProductResponse findBySku(String sku, User currentUser) {
		return productRepository.findBySkuIgnoreCaseAndCompanyId(sku.trim(), currentUser.getCompany().getId())
				.map(ProductMapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con SKU " + sku));
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> findAllByCategoryId(Long categoryId, User currentUser) {
		return productRepository.findAllByCompanyIdAndCategoryIdAndActiveTrueOrderByNameAsc(
                        currentUser.getCompany().getId(), categoryId)
				.stream()
				.map(ProductMapper::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public ProductResponse findById(Long id, User currentUser) {
		return ProductMapper.toResponse(findActiveProduct(id, currentUser.getCompany().getId()));
	}

	@Transactional
	public ProductResponse create(ProductRequest request, User currentUser) {
		ensureSkuAvailable(request.sku(), currentUser.getCompany().getId());
		ensureCategoryExists(request.categoryId(), currentUser.getCompany().getId());
		Product product = ProductMapper.toEntity(request);
		product.setCompany(currentUser.getCompany());
		return ProductMapper.toResponse(productRepository.save(product));
	}

	@Transactional
	public ProductResponse update(Long id, ProductRequest request, User currentUser) {
		Product product = findActiveProduct(id, currentUser.getCompany().getId());
		ensureSkuAvailableForUpdate(request.sku(), id, currentUser.getCompany().getId());
		ensureCategoryExists(request.categoryId(), currentUser.getCompany().getId());
		ProductMapper.updateEntity(product, request);
		return ProductMapper.toResponse(product);
	}

	@Transactional
	public void delete(Long id, User currentUser) {
		Product product = findActiveProduct(id, currentUser.getCompany().getId());
		product.setActive(false);
	}

	private Product findActiveProduct(Long id, Long companyId) {
		return productRepository.findByIdAndCompanyIdAndActiveTrue(id, companyId)
				.orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
	}

	private void ensureSkuAvailable(String sku, Long companyId) {
		if (productRepository.existsBySkuIgnoreCaseAndCompanyId(sku.trim(), companyId)) {
			throw new DuplicateResourceException("Ya existe un producto con el SKU " + sku);
		}
	}

	private void ensureSkuAvailableForUpdate(String sku, Long id, Long companyId) {
		if (productRepository.existsBySkuIgnoreCaseAndCompanyIdAndIdNot(sku.trim(), companyId, id)) {
			throw new DuplicateResourceException("Ya existe un producto con el SKU " + sku);
		}
	}

	private void ensureCategoryExists(Long categoryId, Long companyId) {
		if (categoryId != null && !categoryRepository.existsByIdAndCompanyIdAndActiveTrue(categoryId, companyId)) {
			throw new ResourceNotFoundException("Categoria no encontrada con id " + categoryId);
		}
	}
}
