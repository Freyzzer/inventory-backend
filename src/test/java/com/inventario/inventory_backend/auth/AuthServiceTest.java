package com.inventario.inventory_backend.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.company.CompanyRepository;
import com.inventario.inventory_backend.config.JwtService;
import com.inventario.inventory_backend.exception.DuplicateResourceException;
import com.inventario.inventory_backend.user.Role;
import com.inventario.inventory_backend.user.User;
import com.inventario.inventory_backend.user.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerFirstUserGetsAdminRole() {
        Company company = new Company(1L, "MyCompany", null, true, null, "owner@myco.com", "encoded", Role.OWNER);

        when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("new@test.com")).thenReturn(false);
        when(companyRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(company));
        when(userRepository.findAllByCompanyId(1L)).thenReturn(List.of());
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtService.generateToken("newuser", "ADMIN")).thenReturn("jwt-token");

        RegisterRequest request = new RegisterRequest("newuser", "new@test.com", "password", 1L);
        var result = authService.register(request);

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.user().username()).isEqualTo("newuser");
    }

    @Test
    void registerSecondUserGetsUserRole() {
        Company company = new Company(1L, "MyCompany", null, true, null, "owner@myco.com", "encoded", Role.OWNER);

        when(userRepository.existsByUsernameIgnoreCase("second")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("second@test.com")).thenReturn(false);
        when(companyRepository.findByIdAndActiveTrue(1L)).thenReturn(Optional.of(company));
        when(userRepository.findAllByCompanyId(1L)).thenReturn(List.of(new User()));
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(jwtService.generateToken("second", "USER")).thenReturn("jwt-token");

        RegisterRequest request = new RegisterRequest("second", "second@test.com", "password", 1L);
        var result = authService.register(request);

        assertThat(result.token()).isEqualTo("jwt-token");
    }

    @Test
    void registerThrowsOnDuplicateUsername() {
        when(userRepository.existsByUsernameIgnoreCase("existing")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("existing", "e@t.com", "pass", 1L)))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void registerThrowsWhenCompanyNotFound() {
        when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("new@test.com")).thenReturn(false);
        when(companyRepository.findByIdAndActiveTrue(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(new RegisterRequest("newuser", "new@test.com", "pass", 99L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loginReturnsTokenOnSuccess() {
        User user = new User(1L, "testuser", "test@test.com", "encoded", null, Role.USER, true, null, null);
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtService.generateToken("testuser", "USER")).thenReturn("jwt-token");

        var result = authService.login(new LoginRequest("testuser", "password"));

        assertThat(result.token()).isEqualTo("jwt-token");
    }

    @Test
    void loginThrowsOnWrongPassword() {
        User user = new User(1L, "testuser", "test@test.com", "encoded", null, Role.USER, true, null, null);
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("testuser", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginThrowsOnUnknownUser() {
        when(userRepository.findByUsernameIgnoreCase("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("unknown", "pass")))
                .isInstanceOf(BadCredentialsException.class);
    }
}
