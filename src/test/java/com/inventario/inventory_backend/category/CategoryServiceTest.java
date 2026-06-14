package com.inventario.inventory_backend.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.exception.DuplicateResourceException;
import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import com.inventario.inventory_backend.product.ProductRepository;
import com.inventario.inventory_backend.user.Role;
import com.inventario.inventory_backend.user.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User currentUser;
    private Long companyId = 1L;

    @BeforeEach
    void setUp() {
        Company company = new Company(companyId, "TestCorp", null, true, null, "admin@testcorp.com", "encoded", Role.OWNER);
        currentUser = new User(1L, "admin", "admin@t.com", "pass", company, Role.ADMIN, true, null, null);
    }

    @Test
    void findAll() {
        when(categoryRepository.findAllByCompanyIdAndActiveTrueOrderByNameAsc(companyId))
                .thenReturn(List.of(new Category()));

        assertThat(categoryService.findAll(currentUser)).hasSize(1);
    }

    @Test
    void findByIdThrowsWhenNotFound() {
        when(categoryRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(1L, currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createValidatesNameUniqueness() {
        when(categoryRepository.existsByNameIgnoreCaseAndCompanyId("Books", companyId)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(new CategoryRequest("Books", null), currentUser))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void createSavesCategory() {
        when(categoryRepository.existsByNameIgnoreCaseAndCompanyId("Books", companyId)).thenReturn(false);
        when(categoryRepository.save(any())).thenAnswer(invocation -> {
            Category c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        var result = categoryService.create(new CategoryRequest("Books", "All books"), currentUser);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Books");
    }

    @Test
    void deleteMarksAsInactive() {
        Category category = new Category();
        category.setActive(true);
        when(categoryRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(false);

        categoryService.delete(1L, currentUser);

        assertThat(category.getActive()).isFalse();
    }

    @Test
    void deleteThrowsWhenHasActiveProducts() {
        Category category = new Category();
        category.setActive(true);
        when(categoryRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.of(category));
        when(productRepository.existsByCategoryIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.delete(1L, currentUser))
                .isInstanceOf(IllegalStateException.class);

        assertThat(category.getActive()).isTrue();
    }

    @Test
    void updateWithDuplicateNameThrows() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameIgnoreCaseAndCompanyIdAndIdNot("Duplicate", companyId, 1L)).thenReturn(true);

        assertThatThrownBy(() -> categoryService.update(1L, new CategoryRequest("Duplicate", null), currentUser))
                .isInstanceOf(DuplicateResourceException.class);
    }
}
