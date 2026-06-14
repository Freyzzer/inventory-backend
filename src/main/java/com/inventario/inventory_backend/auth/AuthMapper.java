package com.inventario.inventory_backend.auth;

import com.inventario.inventory_backend.user.User;
import com.inventario.inventory_backend.user.UserMapper;

final class AuthMapper {

    private AuthMapper() {
    }

    static AuthResponse toAuthResponse(String token, User user) {
        return new AuthResponse(token, UserMapper.toResponse(user));
    }
}
