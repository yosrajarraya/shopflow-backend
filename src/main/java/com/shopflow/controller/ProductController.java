package com.shopflow.controller;

import com.shopflow.dto.request.ProductRequest;
import com.shopflow.dto.response.ProductResponse;
import com.shopflow.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion du catalogue produits")
public class ProductController {

    private final ProductService productService;

    // GET /api/products — liste paginée (public)
    @GetMapping
    @Operation(summary = "Lister tous les produits actifs (paginé)")
    public ResponseEntity<Page<ProductResponse>> listerProduits(
            @PageableDefault(size = 12, sort = "dateCreation") Pageable pageable) {
        return ResponseEntity.ok(productService.listerProduits(pageable));
    }

    // GET /api/products/{id} — détail produit (public)
    @GetMapping("/{id}")
    @Operation(summary = "Voir le détail d'un produit avec ses avis et variantes")
    public ResponseEntity<ProductResponse> voirProduit(@PathVariable Long id) {
        return ResponseEntity.ok(productService.voirProduit(id));
    }

    // GET /api/products/search?q= — recherche (public)
    @GetMapping("/search")
    @Operation(summary = "Rechercher des produits par nom ou description")
    public ResponseEntity<Page<ProductResponse>> rechercher(
            @RequestParam String q,
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(productService.rechercher(q, pageable));
    }

    // GET /api/products/top-selling — top ventes (public)
    @GetMapping("/top-selling")
    @Operation(summary = "Top 10 des meilleures ventes")
    public ResponseEntity<List<ProductResponse>> topVentes() {
        return ResponseEntity.ok(productService.topVentes());
    }

    // GET /api/products/my — produits du vendeur connecté
    @GetMapping("/my")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Lister mes produits (vendeur)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<ProductResponse>> mesProduits(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 24, sort = "dateCreation") Pageable pageable) {
        return ResponseEntity.ok(productService.listerMesProduits(userDetails.getUsername(), pageable));
    }

    // GET /api/products/promos — produits en promo (public)
    @GetMapping("/promos")
    @Operation(summary = "Produits en promotion")
    public ResponseEntity<Page<ProductResponse>> promos(
            @PageableDefault(size = 12) Pageable pageable) {
        return ResponseEntity.ok(productService.voirPromos(pageable));
    }

    // POST /api/products — créer (SELLER ou ADMIN)
    @PostMapping
    @Operation(summary = "Créer un produit", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductResponse> creerProduit(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ProductResponse response = productService.creerProduit(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/products/{id} — modifier (SELLER ou ADMIN)
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un produit", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductResponse> modifierProduit(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(productService.modifierProduit(id, request, userDetails.getUsername()));
    }

    // DELETE /api/products/{id} — désactiver soft delete (SELLER ou ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    @Operation(summary = "Désactiver un produit (soft delete)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> desactiverProduit(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        productService.desactiverProduit(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
