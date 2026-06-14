package com.inventario.inventory_backend.auth;

import com.inventario.inventory_backend.company.Company;
import com.inventario.inventory_backend.company.CompanyRepository;
import com.inventario.inventory_backend.config.JwtService;
import com.inventario.inventory_backend.exception.DuplicateResourceException;
import com.inventario.inventory_backend.user.Role;
import com.inventario.inventory_backend.user.User;
import com.inventario.inventory_backend.user.UserMapper;
import com.inventario.inventory_backend.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, CompanyRepository companyRepository,
                       JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameIgnoreCase(request.username().trim())) {
            throw new DuplicateResourceException("El usuario " + request.username() + " ya esta registrado");
        }
        if (userRepository.existsByEmailIgnoreCase(request.email().trim())) {
            throw new DuplicateResourceException("El email " + request.email() + " ya esta registrado");
        }

        Company company = companyRepository.findByIdAndActiveTrue(request.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada con id " + request.companyId()));

        boolean isFirstUser = userRepository.findAllByCompanyId(company.getId()).isEmpty();
        Role role = isFirstUser ? Role.ADMIN : Role.USER;

        User user = userRepository.save(
                UserMapper.toEntity(request.username(), request.email(),
                        passwordEncoder.encode(request.password()), company, role));

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return AuthMapper.toAuthResponse(token, user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsernameIgnoreCase(request.username().trim())
                .orElseThrow(() -> new BadCredentialsException("Usuario o contrasena incorrectos"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Usuario o contrasena incorrectos");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return AuthMapper.toAuthResponse(token, user);
    }
}
