package com.inventario.inventory_backend.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.company.CompanyRepository;
import com.inventario.inventory_backend.user.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;

    @BeforeEach
    void setUp() {
        company = companyRepository.save(new Company(null, "TestCorp", null, true, null, "admin@testcorp.com", "encoded", Role.OWNER));
    }

    @Test
    void saveAndFindById() {
        Category category = new Category(null, "Electronics", "Electronic items", company, null, true, null, null);
        category = categoryRepository.save(category);

        assertThat(category.getId()).isNotNull();
        assertThat(category.getCreatedAt()).isNotNull();
    }

    @Test
    void findActiveCategories() {
        categoryRepository.save(new Category(null, "ActiveCat", null, company, null, true, null, null));
        categoryRepository.save(new Category(null, "InactiveCat", null, company, null, false, null, null));

        var result = categoryRepository.findAllByCompanyIdAndActiveTrueOrderByNameAsc(company.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("ActiveCat");
    }

    @Test
    void existsByNameIgnoreCase() {
        categoryRepository.save(new Category(null, "Electronics", null, company, null, true, null, null));

        assertThat(categoryRepository.existsByNameIgnoreCaseAndCompanyId("electronics", company.getId())).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCaseAndCompanyId("Books", company.getId())).isFalse();
    }

    @Test
    void existsByIdAndActiveTrue() {
        Category c = categoryRepository.save(new Category(null, "Test", null, company, null, true, null, null));

        assertThat(categoryRepository.existsByIdAndCompanyIdAndActiveTrue(c.getId(), company.getId())).isTrue();
        assertThat(categoryRepository.existsByIdAndCompanyIdAndActiveTrue(c.getId() + 999, company.getId())).isFalse();
    }
}
