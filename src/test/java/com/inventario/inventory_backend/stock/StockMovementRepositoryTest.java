package com.inventario.inventory_backend.stock;

import static org.assertj.core.api.Assertions.assertThat;

import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.company.CompanyRepository;
import com.inventario.inventory_backend.product.Product;
import com.inventario.inventory_backend.product.ProductRepository;
import com.inventario.inventory_backend.user.Role;
import com.inventario.inventory_backend.user.User;
import com.inventario.inventory_backend.user.UserRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StockMovementRepositoryTest {

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;

    @BeforeEach
    void setUp() {
        company = companyRepository.save(new Company(null, "TestCorp", null, true, null, "admin@testcorp.com", "encoded", Role.OWNER));
    }

    @Test
    void saveAndFindByProductId() {
        User user = userRepository.save(
                new User(null, "stockuser", "stock@test.com", "pass", company, Role.ADMIN, true, null, null));

        Product product = productRepository.save(
                new Product(null, "SKU-MOV", "Movement Test", null, BigDecimal.TEN, 10, null, company, true, null, null, 0));

        StockMovement in = new StockMovement(null, product, MovementType.IN, 5, null, "REF-001", "Initial stock", null, user);
        StockMovement out = new StockMovement(null, product, MovementType.OUT, 2, null, "REF-002", "Sale", null, user);
        stockMovementRepository.save(in);
        stockMovementRepository.save(out);

        var movements = stockMovementRepository.findAllByProductIdOrderByCreatedAtDesc(product.getId());

        assertThat(movements).hasSize(2);
        assertThat(movements.getFirst().getReference()).isEqualTo("REF-002");
        assertThat(movements.getFirst().getProduct().getId()).isEqualTo(product.getId());
    }
}
