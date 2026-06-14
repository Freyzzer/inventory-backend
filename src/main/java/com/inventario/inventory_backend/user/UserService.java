package com.inventario.inventory_backend.user;

import com.inventario.inventory_backend.exception.DuplicateResourceException;
import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll(User currentUser) {
        return userRepository.findAllByCompanyId(currentUser.getCompany().getId())
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id, User currentUser) {
        return userRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .map(UserMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(User currentUser) {
        return UserMapper.toResponse(currentUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request, User currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        if (!isAdmin && !currentUser.getId().equals(id)) {
            throw new SecurityException("No tienes permiso para modificar este usuario");
        }

        User user = userRepository.findByIdAndCompanyId(id, currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));

        if (request.username() != null && !request.username().trim().equalsIgnoreCase(user.getUsername())) {
            if (userRepository.existsByUsernameIgnoreCase(request.username().trim())) {
                throw new DuplicateResourceException("El usuario " + request.username() + " ya esta registrado");
            }
            user.setUsername(request.username().trim());
        }

        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        return UserMapper.toResponse(user);
    }
}
