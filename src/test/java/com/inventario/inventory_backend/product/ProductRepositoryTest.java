package com.inventario.inventory_backend.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.company.CompanyRepository;
import com.inventario.inventory_backend.user.Role;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;

    @BeforeEach
    void setUp() {
        company = companyRepository.save(new Company(null, "TestCorp", null, true, null, "admin@testcorp.com", "encoded", Role.OWNER));
    }

    @Test
    void saveAndFindById() {
        Product product = new Product(null, "SKU-001", "Test Product", "Description",
                BigDecimal.TEN, 5, null, company, true, null, null, 0);
        product = productRepository.save(product);

        assertThat(product.getId()).isNotNull();
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    void findByActiveTrue() {
        Product active = new Product(null, "SKU-ACTIVE", "Active", null, BigDecimal.ONE, 1, null, company, true, null, null, 0);
        Product inactive = new Product(null, "SKU-INACTIVE", "Inactive", null, BigDecimal.ONE, 1, null, company, false, null, null, 0);
        productRepository.save(active);
        productRepository.save(inactive);

        var result = productRepository.findAllByCompanyIdAndActiveTrueOrderByNameAsc(company.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getSku()).isEqualTo("SKU-ACTIVE");
    }

    @Test
    void existsBySkuIgnoreCase() {
        productRepository.save(new Product(null, "SKU-001", "Test", null, BigDecimal.ONE, 1, null, company, true, null, null, 0));

        assertThat(productRepository.existsBySkuIgnoreCaseAndCompanyId("SKU-001", company.getId())).isTrue();
        assertThat(productRepository.existsBySkuIgnoreCaseAndCompanyId("NOT-EXISTS", company.getId())).isFalse();
    }

    @Test
    void existsBySkuIgnoreCaseAndIdNot() {
        Product p = productRepository.save(
                new Product(null, "SKU-001", "Test", null, BigDecimal.ONE, 1, null, company, true, null, null, 0));

        assertThat(productRepository.existsBySkuIgnoreCaseAndCompanyIdAndIdNot("sku-001", company.getId(), p.getId())).isFalse();
    }
}
