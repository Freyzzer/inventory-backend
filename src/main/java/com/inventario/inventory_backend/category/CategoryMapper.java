package com.inventario.inventory_backend.category;

final class CategoryMapper {

	private CategoryMapper() {
	}

	static Category toEntity(CategoryRequest request) {
		Category category = new Category();
		updateEntity(category, request);
		category.setActive(true);
		return category;
	}

	static void updateEntity(Category category, CategoryRequest request) {
		category.setName(request.name().trim());
		category.setDescription(request.description());
	}

	static CategoryResponse toResponse(Category category) {
		return new CategoryResponse(
				category.getId(),
				category.getName(),
				category.getDescription(),
				category.getActive(),
				category.getCreatedAt(),
				category.getUpdatedAt()
		);
	}
}
