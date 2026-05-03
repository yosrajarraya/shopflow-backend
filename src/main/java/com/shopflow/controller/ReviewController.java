package com.shopflow.controller;

import com.shopflow.dto.request.ReviewRequest;
import com.shopflow.dto.response.ReviewResponse;
import com.shopflow.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Avis", description = "Gestion des avis et notations")
public class ReviewController {

    private final ReviewService reviewService;

    // POST /api/reviews — poster un avis (CUSTOMER, achat vérifié)
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Poster un avis sur un produit acheté",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> posterAvis(
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReviewResponse response = reviewService.posterAvis(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/reviews/product/{productId} — avis d'un produit (public)
    @GetMapping("/product/{productId}")
    @Operation(summary = "Voir les avis approuvés d'un produit")
    public ResponseEntity<List<ReviewResponse>> voirAvisProduit(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.voirAvisProduit(productId));
    }

    // PUT /api/reviews/{id}/approve — approuver un avis (ADMIN)
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approuver un avis (modération)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> approuverAvis(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approuverAvis(id));
    }

    // PUT /api/reviews/{id}/disapprove — désapprouver un avis (ADMIN)
    @PutMapping("/{id}/disapprove")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désapprouver un avis (masquer sans supprimer)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> desapprouverAvis(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.desapprouverAvis(id));
    }

    // GET /api/reviews/seller — voir les avis pour les produits du vendeur connecté
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Voir les avis des produits du vendeur",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ReviewResponse>> voirAvisVendeur(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reviewService.voirAvisVendeur(userDetails.getUsername()));
    }

    // GET /api/reviews — tous les avis (ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les avis (Admin)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ReviewResponse>> listerTousAvis() {
        return ResponseEntity.ok(reviewService.listerTousAvis());
    }

    // GET /api/reviews/pending — avis en attente (ADMIN)
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister les avis en attente de modération (Admin)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ReviewResponse>> listerAvisEnAttente() {
        return ResponseEntity.ok(reviewService.listerAvisEnAttente());
    }

    // DELETE /api/reviews/{id} — rejeter/supprimer un avis (ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rejeter un avis (Admin)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> rejeterAvis(@PathVariable Long id) {
        reviewService.rejeterAvis(id);
        return ResponseEntity.noContent().build();
    }
}
