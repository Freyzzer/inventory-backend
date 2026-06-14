package com.inventario.inventory_backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.inventario.inventory_backend.category.CategoryRepository;
import com.inventario.inventory_backend.config.JwtService;
import com.inventario.inventory_backend.product.ProductRepository;
import com.inventario.inventory_backend.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InventoryBackendApplicationTests {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void contextLoads() {
        assertThat(productRepository).isNotNull();
        assertThat(categoryRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(jwtService).isNotNull();
    }

    @Test
    void jwtServiceGeneratesAndValidatesToken() {
        String token = jwtService.generateToken("testuser", "USER");
        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void jwtServiceRejectsInvalidToken() {
        assertThat(jwtService.isTokenValid("invalid.token.here")).isFalse();
    }
}
