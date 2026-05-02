package com.shopflow.controller;

import com.shopflow.dto.request.CategoryRequest;
import com.shopflow.dto.response.CategoryResponse;
import com.shopflow.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories", description = "Gestion des catégories de produits")
public class CategoryController {

    private final CategoryService categoryService;

    // GET /api/categories — arbre de catégories (public)
    @GetMapping
    @Operation(summary = "Voir l'arbre de toutes les catégories")
    public ResponseEntity<List<CategoryResponse>> listerCategories() {
        return ResponseEntity.ok(categoryService.listerCategories());
    }

    // POST /api/categories — créer (ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une catégorie", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryResponse> creerCategorie(
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.creerCategorie(request));
    }

    // PUT /api/categories/{id} — modifier (ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une catégorie", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryResponse> modifierCategorie(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.modifierCategorie(id, request));
    }

    // DELETE /api/categories/{id} — supprimer (ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une catégorie", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> supprimerCategorie(@PathVariable Long id) {
        categoryService.supprimerCategorie(id);
        return ResponseEntity.noContent().build();
    }
}
