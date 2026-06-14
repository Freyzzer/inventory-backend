package com.inventario.inventory_backend.auth;

import com.inventario.inventory_backend.user.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
