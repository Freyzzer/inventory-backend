package com.inventario.inventory_backend.stock;

import java.math.BigDecimal;
import java.time.Instant;

public record StockMovementResponse(
        Long id,
        Long productId,
        String productSku,
        String productName,
        MovementType type,
        Integer quantity,
        BigDecimal unitPrice,
        Integer stockAfter,
        String reference,
        String notes,
        Instant createdAt
) {
}
