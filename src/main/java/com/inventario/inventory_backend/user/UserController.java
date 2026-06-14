package com.inventario.inventory_backend.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestion de usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Listar usuarios (solo admin)")
    @GetMapping
    List<UserResponse> findAll(@AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Acceso denegado");
        }
        return userService.findAll(currentUser);
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    UserResponse findById(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.ADMIN && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Acceso denegado");
        }
        return userService.findById(id, currentUser);
    }

    @Operation(summary = "Obtener perfil del usuario autenticado")
    @GetMapping("/me")
    UserResponse getProfile(@AuthenticationPrincipal User currentUser) {
        return userService.getProfile(currentUser);
    }

    @Operation(summary = "Actualizar usuario", description = "El usuario puede modificar su propio perfil. El admin puede modificar cualquier usuario.")
    @PutMapping("/{id}")
    UserResponse update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request,
                        @AuthenticationPrincipal User currentUser) {
        return userService.updateUser(id, request, currentUser);
    }
}
