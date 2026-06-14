package com.inventario.inventory_backend.stock;

import com.inventario.inventory_backend.exception.ResourceNotFoundException;
import com.inventario.inventory_backend.product.Product;
import com.inventario.inventory_backend.product.ProductRepository;
import com.inventario.inventory_backend.user.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    public StockMovementService(StockMovementRepository stockMovementRepository, ProductRepository productRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<StockMovementResponse> findByProductId(Long productId) {
        return stockMovementRepository.findAllByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(m -> StockMovementMapper.toResponse(m, null))
                .toList();
    }

    @Transactional
    public StockMovementResponse register(StockMovementRequest request, User currentUser) {
        Product product = productRepository.findByIdAndCompanyIdAndActiveTrue(
                        request.productId(), currentUser.getCompany().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + request.productId()));

        int newStock;
        if (request.type() == MovementType.IN) {
            newStock = product.getStockQuantity() + request.quantity();
        } else {
            int result = product.getStockQuantity() - request.quantity();
            if (result < 0) {
                throw new IllegalStateException(
                        "Stock insuficiente. Stock actual: " + product.getStockQuantity() +
                        ", intentaste sacar: " + request.quantity());
            }
            newStock = result;
        }

        product.setStockQuantity(newStock);
        StockMovement movement = stockMovementRepository.save(StockMovementMapper.toEntity(product, request, currentUser));

        return StockMovementMapper.toResponse(movement, newStock);
    }
}
