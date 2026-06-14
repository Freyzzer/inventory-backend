package com.inventario.inventory_backend.category;

import com.inventario.inventory_backend.exception.DuplicateResourceException;
import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import com.inventario.inventory_backend.product.ProductRepository;
import com.inventario.inventory_backend.user.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll(User currentUser) {
        return categoryRepository.findAllByCompanyIdAndActiveTrueOrderByNameAsc(currentUser.getCompany().getId())
                .stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id, User currentUser) {
        return CategoryMapper.toResponse(findActiveCategory(id, currentUser.getCompany().getId()));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request, User currentUser) {
        ensureNameAvailable(request.name(), currentUser.getCompany().getId());
        Category category = CategoryMapper.toEntity(request);
        category.setCompany(currentUser.getCompany());
        return CategoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request, User currentUser) {
        Category category = findActiveCategory(id, currentUser.getCompany().getId());
        ensureNameAvailableForUpdate(request.name(), id, currentUser.getCompany().getId());
        CategoryMapper.updateEntity(category, request);
        return CategoryMapper.toResponse(category);
    }

    @Transactional
    public void delete(Long id, User currentUser) {
        Long companyId = currentUser.getCompany().getId();
        Category category = findActiveCategory(id, companyId);
        if (productRepository.existsByCategoryIdAndCompanyIdAndActiveTrue(id, companyId)) {
            throw new IllegalStateException("No se puede desactivar la categoria porque tiene productos activos asociados");
        }
        category.setActive(false);
    }

    private Category findActiveCategory(Long id, Long companyId) {
        return categoryRepository.findByIdAndCompanyIdAndActiveTrue(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada con id " + id));
    }

    private void ensureNameAvailable(String name, Long companyId) {
        if (categoryRepository.existsByNameIgnoreCaseAndCompanyId(name.trim(), companyId)) {
            throw new DuplicateResourceException("Ya existe una categoria con el nombre " + name);
        }
    }

    private void ensureNameAvailableForUpdate(String name, Long id, Long companyId) {
        if (categoryRepository.existsByNameIgnoreCaseAndCompanyIdAndIdNot(name.trim(), companyId, id)) {
            throw new DuplicateResourceException("Ya existe una categoria con el nombre " + name);
        }
    }
}
