package com.inventario.inventory_backend.user;

import com.inventario.inventory_backend.company.Company;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(String username, String email, String encodedPassword, Company company, Role role) {
        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPassword(encodedPassword);
        user.setCompany(company);
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
