package com.inventario.inventory_backend.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.exception.DuplicateResourceException;
import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User currentUser;
    private Company company;
    private Long companyId = 1L;

    @BeforeEach
    void setUp() {
        company = new Company(companyId, "TestCorp", null, true, null, "admin@testcorp.com", "encoded", Role.OWNER);
        currentUser = new User(1L, "admin", "admin@t.com", "pass", company, Role.ADMIN, true, null, null);
    }

    @Test
    void findAll() {
        when(userRepository.findAllByCompanyId(companyId)).thenReturn(List.of(new User()));

        assertThat(userService.findAll(currentUser)).hasSize(1);
    }

    @Test
    void findById() {
        User user = new User(1L, "test", "test@test.com", "pass", company, Role.USER, true, null, null);
        when(userRepository.findByIdAndCompanyId(1L, companyId)).thenReturn(Optional.of(user));

        var result = userService.findById(1L, currentUser);

        assertThat(result.username()).isEqualTo("test");
    }

    @Test
    void findByIdThrowsWhenNotFound() {
        when(userRepository.findByIdAndCompanyId(99L, companyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L, currentUser))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getProfile() {
        User user = new User(1L, "test", "test@test.com", "pass", company, Role.USER, true, null, null);

        var result = userService.getProfile(user);

        assertThat(result.username()).isEqualTo("test");
        assertThat(result.email()).isEqualTo("test@test.com");
    }

    @Test
    void updateOwnProfile() {
        User existingUser = new User(1L, "test", "test@test.com", "pass", company, Role.USER, true, null, null);

        when(userRepository.findByIdAndCompanyId(1L, companyId)).thenReturn(Optional.of(existingUser));

        var result = userService.updateUser(1L, new UserUpdateRequest("newuser", null), currentUser);

        assertThat(result.username()).isEqualTo("newuser");
    }

    @Test
    void updateThrowsWhenAnotherUserTries() {
        User current = new User(1L, "user1", "u1@t.com", "pass", company, Role.USER, true, null, null);

        assertThatThrownBy(() -> userService.updateUser(2L, new UserUpdateRequest("hacker", null), current))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void updateThrowsOnDuplicateUsername() {
        User existingUser = new User(1L, "test", "test@test.com", "pass", company, Role.USER, true, null, null);

        when(userRepository.findByIdAndCompanyId(1L, companyId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsernameIgnoreCase("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, new UserUpdateRequest("taken", null), currentUser))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void adminCanUpdateAnyUser() {
        User target = new User(2L, "user2", "u2@t.com", "pass", company, Role.USER, true, null, null);

        when(userRepository.findByIdAndCompanyId(2L, companyId)).thenReturn(Optional.of(target));

        var result = userService.updateUser(2L, new UserUpdateRequest(null, "newpass"), currentUser);

        assertThat(result.username()).isEqualTo("user2");
    }
}
