package com.inventario.inventory_backend.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
		@NotBlank(message = "El nombre es obligatorio")
		@Size(max = 120, message = "El nombre no puede superar 120 caracteres")
		String name,

		@Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
		String description
) {
}
