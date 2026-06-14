package com.inventario.inventory_backend.user;

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
class UserRepositoryTest {

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
    void saveAndFindByUsername() {
        User user = new User(null, "testuser", "test@test.com", "encoded", company, Role.USER, true, null, null);
        user = userRepository.save(user);

        assertThat(user.getId()).isNotNull();

        var found = userRepository.findByUsernameIgnoreCase("TESTUSER");
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void existsByUsernameAndEmail() {
        userRepository.save(new User(null, "testuser", "test@test.com", "encoded", company, Role.USER, true, null, null));

        assertThat(userRepository.existsByUsernameIgnoreCase("testuser")).isTrue();
        assertThat(userRepository.existsByEmailIgnoreCase("test@test.com")).isTrue();
        assertThat(userRepository.existsByUsernameIgnoreCase("other")).isFalse();
    }
}
