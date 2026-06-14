package com.inventario.inventory_backend.stock;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findAllByProductIdOrderByCreatedAtDesc(Long productId);
}
