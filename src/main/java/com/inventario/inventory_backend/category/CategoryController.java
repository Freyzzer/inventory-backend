package com.inventario.inventory_backend.category;

import com.inventario.inventory_backend.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categorias", description = "CRUD de categorias")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "Listar categorias activas")
    @GetMapping
    List<CategoryResponse> findAll(@AuthenticationPrincipal User currentUser) {
        return categoryService.findAll(currentUser);
    }

    @Operation(summary = "Obtener categoria por ID")
    @GetMapping("/{id}")
    CategoryResponse findById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        return categoryService.findById(id, currentUser);
    }

    @Operation(summary = "Crear categoria")
    @PostMapping
    ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request,
                                             @AuthenticationPrincipal User currentUser) {
        CategoryResponse category = categoryService.create(request, currentUser);
        return ResponseEntity.created(URI.create("/api/categories/" + category.id())).body(category);
    }

    @Operation(summary = "Actualizar categoria")
    @PutMapping("/{id}")
    CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request,
                             @AuthenticationPrincipal User currentUser) {
        return categoryService.update(id, request, currentUser);
    }

    @Operation(summary = "Eliminar categoria (borrado logico)", description = "Solo se puede desactivar si no tiene productos activos asociados")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        categoryService.delete(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
