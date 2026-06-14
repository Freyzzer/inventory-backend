package com.inventario.inventory_backend.product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
		Long id,
		String sku,
		String name,
		String description,
		BigDecimal unitPrice,
		Integer stockQuantity,
		Long categoryId,
		String categoryName,
		Boolean active,
		Instant createdAt,
		Instant updatedAt
) {
}
