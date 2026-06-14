package com.inventario.inventory_backend.stock;

import com.inventario.inventory_backend.product.Product;
import com.inventario.inventory_backend.user.User;

final class StockMovementMapper {

    private StockMovementMapper() {
    }

    static StockMovement toEntity(Product product, StockMovementRequest request, User user) {
        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setType(request.type());
        movement.setQuantity(request.quantity());
        movement.setUnitPrice(request.unitPrice());
        movement.setReference(request.reference());
        movement.setNotes(request.notes());
        movement.setUser(user);
        return movement;
    }

    static StockMovementResponse toResponse(StockMovement movement, Integer stockAfter) {
        return new StockMovementResponse(
                movement.getId(),
                movement.getProduct().getId(),
                movement.getProduct().getSku(),
                movement.getProduct().getName(),
                movement.getType(),
                movement.getQuantity(),
                movement.getUnitPrice(),
                stockAfter,
                movement.getReference(),
                movement.getNotes(),
                movement.getCreatedAt()
        );
    }
}
