package com.inventario.inventory_backend.stock;

import com.inventario.inventory_backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock-movements")
@Tag(name = "Movimientos de Stock", description = "Registro y consulta de movimientos de inventario")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @Operation(summary = "Historial de movimientos por producto")
    @GetMapping("/product/{productId}")
    List<StockMovementResponse> findByProductId(@PathVariable Long productId) {
        return stockMovementService.findByProductId(productId);
    }

    @Operation(summary = "Registrar movimiento de stock", description = "IN para entrada, OUT para salida. Actualiza el stock del producto automaticamente")
    @PostMapping
    ResponseEntity<StockMovementResponse> register(@Valid @RequestBody StockMovementRequest request,
                                                    @AuthenticationPrincipal User currentUser) {
        StockMovementResponse movement = stockMovementService.register(request, currentUser);
        return ResponseEntity.created(URI.create("/api/stock-movements/product/" + request.productId())).body(movement);
    }
}
