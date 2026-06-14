package com.inventario.inventory_backend.product;

import com.inventario.inventory_backend.category.Category;

final class ProductMapper {

	private ProductMapper() {
	}

	static Product toEntity(ProductRequest request) {
		Product product = new Product();
		updateEntity(product, request);
		product.setActive(true);
		return product;
	}

	static void updateEntity(Product product, ProductRequest request) {
		product.setSku(request.sku().trim());
		product.setName(request.name().trim());
		product.setDescription(request.description());
		product.setUnitPrice(request.unitPrice());
		product.setStockQuantity(request.stockQuantity());
		if (request.categoryId() != null) {
			product.setCategory(new Category());
			product.getCategory().setId(request.categoryId());
		} else {
			product.setCategory(null);
		}
	}

	static ProductResponse toResponse(Product product) {
		return new ProductResponse(
				product.getId(),
				product.getSku(),
				product.getName(),
				product.getDescription(),
				product.getUnitPrice(),
				product.getStockQuantity(),
				product.getCategory() != null ? product.getCategory().getId() : null,
				product.getCategory() != null ? product.getCategory().getName() : null,
				product.getActive(),
				product.getCreatedAt(),
				product.getUpdatedAt()
		);
	}
}
