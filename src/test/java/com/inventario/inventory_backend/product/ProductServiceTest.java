package com.inventario.inventory_backend.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inventario.inventory_backend.category.CategoryRepository;
import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.exception.DuplicateResourceException;
import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import com.inventario.inventory_backend.user.Role;
import com.inventario.inventory_backend.user.User;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private User currentUser;
    private Long companyId = 1L;

    @BeforeEach
    void setUp() {
        Company company = new Company(companyId, "TestCorp", null, true, null, "admin@testcorp.com", "encoded", Role.OWNER);
        currentUser = new User(1L, "admin", "admin@t.com", "pass", company, Role.ADMIN, true, null, null);
    }

    @Test
    void findAllReturnsOnlyActive() {
        when(productRepository.findAllByCompanyIdAndActiveTrueOrderByNameAsc(companyId))
                .thenReturn(List.of(new Product()));

        var result = productService.findAll(currentUser);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByIdThrowsWhenNotFound() {
        when(productRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(1L, currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createValidatesSkuUniqueness() {
        when(productRepository.existsBySkuIgnoreCaseAndCompanyId("SKU-001", companyId)).thenReturn(true);

        ProductRequest request = new ProductRequest("SKU-001", "Name", null, BigDecimal.ONE, 1, null);

        assertThatThrownBy(() -> productService.create(request, currentUser))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void createSavesProduct() {
        when(productRepository.existsBySkuIgnoreCaseAndCompanyId("SKU-001", companyId)).thenReturn(false);
        when(categoryRepository.existsByIdAndCompanyIdAndActiveTrue(any(), eq(companyId))).thenReturn(true);
        when(productRepository.save(any())).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductRequest request = new ProductRequest("SKU-001", "Test Product", "desc", BigDecimal.valueOf(10.5), 5, 1L);

        var result = productService.create(request, currentUser);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.sku()).isEqualTo("SKU-001");
        assertThat(result.categoryId()).isEqualTo(1L);
    }

    @Test
    void createWithNullCategoryIdSavesWithoutCategory() {
        when(productRepository.existsBySkuIgnoreCaseAndCompanyId("SKU-002", companyId)).thenReturn(false);
        when(productRepository.save(any())).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        ProductRequest request = new ProductRequest("SKU-002", "No Cat", null, BigDecimal.ONE, 1, null);

        var result = productService.create(request, currentUser);

        assertThat(result.categoryId()).isNull();
        assertThat(result.categoryName()).isNull();
    }

    @Test
    void deleteMarksAsInactive() {
        Product product = new Product();
        product.setActive(true);
        when(productRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.of(product));

        productService.delete(1L, currentUser);

        assertThat(product.getActive()).isFalse();
    }

    @Test
    void deleteThrowsWhenNotFound() {
        when(productRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.delete(1L, currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateValidatesSkuForOtherProducts() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setSku("SKU-OLD");
        when(productRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.of(existing));
        when(productRepository.existsBySkuIgnoreCaseAndCompanyIdAndIdNot("SKU-NEW", companyId, 1L)).thenReturn(true);

        ProductRequest request = new ProductRequest("SKU-NEW", "Updated", null, BigDecimal.ONE, 1, null);

        assertThatThrownBy(() -> productService.update(1L, request, currentUser))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void findAllByCategoryId() {
        when(productRepository.findAllByCompanyIdAndCategoryIdAndActiveTrueOrderByNameAsc(companyId, 1L))
                .thenReturn(List.of(new Product()));

        var result = productService.findAllByCategoryId(1L, currentUser);

        assertThat(result).hasSize(1);
        verify(productRepository).findAllByCompanyIdAndCategoryIdAndActiveTrueOrderByNameAsc(companyId, 1L);
    }

    @Test
    void searchWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Product())));

        var result = productService.search("name", "sku", 1L, BigDecimal.ONE, BigDecimal.TEN, true, pageable, currentUser);

        assertThat(result).hasSize(1);
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void findBySkuReturnsProduct() {
        Product product = new Product();
        product.setSku("SKU-001");
        product.setName("Test");
        when(productRepository.findBySkuIgnoreCaseAndCompanyId("sku-001", companyId)).thenReturn(Optional.of(product));

        var result = productService.findBySku("sku-001", currentUser);

        assertThat(result.sku()).isEqualTo("SKU-001");
    }

    @Test
    void findBySkuThrowsWhenNotFound() {
        when(productRepository.findBySkuIgnoreCaseAndCompanyId("UNKNOWN", companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findBySku("UNKNOWN", currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
