package com.inventario.inventory_backend.stock;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record StockMovementRequest(
        @NotNull(message = "El producto es obligatorio")
        Long productId,

        @NotNull(message = "El tipo de movimiento es obligatorio")
        MovementType type,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer quantity,

        BigDecimal unitPrice,

        @Size(max = 100, message = "La referencia no puede superar 100 caracteres")
        String reference,

        @Size(max = 500, message = "Las notas no pueden superar 500 caracteres")
        String notes
) {
}
