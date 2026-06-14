package com.inventario.inventory_backend.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import com.inventario.inventory_backend.product.Product;
import com.inventario.inventory_backend.product.ProductRepository;
import com.inventario.inventory_backend.user.Role;
import com.inventario.inventory_backend.user.User;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StockMovementService stockMovementService;

    private User currentUser;
    private Long companyId = 1L;

    @BeforeEach
    void setUp() {
        Company company = new Company(companyId, "TestCorp", null, true, null, "admin@testcorp.com", "encoded", Role.OWNER);
        currentUser = new User(1L, "admin", "admin@t.com", "pass", company, Role.ADMIN, true, null, null);
    }

    @Test
    void registerInIncreasesStock() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");
        product.setName("Test");
        product.setStockQuantity(10);
        product.setActive(true);

        when(productRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.of(product));
        when(stockMovementRepository.save(any())).thenAnswer(invocation -> {
            StockMovement m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        StockMovementRequest request = new StockMovementRequest(1L, MovementType.IN, 5, null, null, null);
        var result = stockMovementService.register(request, currentUser);

        assertThat(result.stockAfter()).isEqualTo(15);
        assertThat(product.getStockQuantity()).isEqualTo(15);
    }

    @Test
    void registerOutDecreasesStock() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU-001");
        product.setName("Test");
        product.setStockQuantity(10);
        product.setActive(true);

        when(productRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.of(product));
        when(stockMovementRepository.save(any())).thenAnswer(invocation -> {
            StockMovement m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        StockMovementRequest request = new StockMovementRequest(1L, MovementType.OUT, 3, null, null, null);
        var result = stockMovementService.register(request, currentUser);

        assertThat(result.stockAfter()).isEqualTo(7);
        assertThat(product.getStockQuantity()).isEqualTo(7);
    }

    @Test
    void registerOutThrowsWhenInsufficientStock() {
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(2);
        product.setActive(true);

        when(productRepository.findByIdAndCompanyIdAndActiveTrue(1L, companyId)).thenReturn(Optional.of(product));

        StockMovementRequest request = new StockMovementRequest(1L, MovementType.OUT, 5, null, null, null);

        assertThatThrownBy(() -> stockMovementService.register(request, currentUser))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void registerThrowsWhenProductNotFound() {
        when(productRepository.findByIdAndCompanyIdAndActiveTrue(99L, companyId)).thenReturn(Optional.empty());

        StockMovementRequest request = new StockMovementRequest(99L, MovementType.IN, 1, null, null, null);

        assertThatThrownBy(() -> stockMovementService.register(request, currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
