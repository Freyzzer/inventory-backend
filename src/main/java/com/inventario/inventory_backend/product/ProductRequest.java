package com.inventario.inventory_backend.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
		@NotBlank(message = "El SKU es obligatorio")
		@Size(max = 80, message = "El SKU no puede superar 80 caracteres")
		String sku,

		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 160, message = "El nombre no puede superar 160 caracteres")
		String name,

		@Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
		String description,

		@NotNull(message = "El precio unitario es obligatorio")
		@DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor a 0")
		BigDecimal unitPrice,

		@NotNull(message = "La cantidad en stock es obligatoria")
		@Min(value = 0, message = "La cantidad en stock no puede ser negativa")
		Integer stockQuantity,

		Long categoryId
) {
}
