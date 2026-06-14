package com.inventario.inventory_backend.company;

import java.time.Instant;

public record CompanyResponse(
        Long id,
        String name,
        String plan,
        Boolean active,
        Instant createdAt
) {
}
