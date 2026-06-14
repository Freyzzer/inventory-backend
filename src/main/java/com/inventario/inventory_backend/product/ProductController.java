package com.inventario.inventory_backend.product;

import com.inventario.inventory_backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "CRUD de productos")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@Operation(summary = "Listar productos activos", description = "Retorna todos los productos activos, opcionalmente filtrados por categoría")
	@GetMapping
	List<ProductResponse> findAll(@RequestParam(required = false) Long categoryId,
                                   @AuthenticationPrincipal User currentUser) {
		if (categoryId != null) {
			return productService.findAllByCategoryId(categoryId, currentUser);
		}
		return productService.findAll(currentUser);
	}

	@Operation(summary = "Buscar productos", description = "Búsqueda paginada con filtros por nombre, SKU, categoría y rango de precios")
	@GetMapping("/search")
	Page<ProductResponse> search(
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String sku,
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice,
			@RequestParam(required = false) Boolean active,
			Pageable pageable,
            @AuthenticationPrincipal User currentUser) {
		return productService.search(name, sku, categoryId, minPrice, maxPrice, active, pageable, currentUser);
	}

	@Operation(summary = "Buscar producto por SKU", description = "Retorna un producto por su SKU exacto")
	@GetMapping("/sku/{sku}")
	ProductResponse findBySku(@PathVariable String sku, @AuthenticationPrincipal User currentUser) {
		return productService.findBySku(sku, currentUser);
	}

	@Operation(summary = "Obtener producto por ID")
	@GetMapping("/{id}")
	ProductResponse findById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
		return productService.findById(id, currentUser);
	}

	@Operation(summary = "Crear producto")
	@PostMapping
	ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request,
                                            @AuthenticationPrincipal User currentUser) {
		ProductResponse product = productService.create(request, currentUser);
		return ResponseEntity.created(URI.create("/api/products/" + product.id())).body(product);
	}

	@Operation(summary = "Actualizar producto")
	@PutMapping("/{id}")
	ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request,
                            @AuthenticationPrincipal User currentUser) {
		return productService.update(id, request, currentUser);
	}

	@Operation(summary = "Eliminar producto (borrado lógico)")
	@DeleteMapping("/{id}")
	ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
		productService.delete(id, currentUser);
		return ResponseEntity.noContent().build();
	}
}
