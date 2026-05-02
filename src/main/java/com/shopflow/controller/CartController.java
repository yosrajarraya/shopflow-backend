package com.shopflow.controller;

import com.shopflow.dto.request.CartItemRequest;
import com.shopflow.dto.response.CartResponse;
import com.shopflow.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Panier", description = "Gestion du panier d'achat")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    // GET /api/cart — voir son panier
    @GetMapping
    @Operation(summary = "Voir le panier du client connecté")
    public ResponseEntity<CartResponse> voirPanier(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.voirPanier(userDetails.getUsername()));
    }

    // POST /api/cart/items — ajouter un article
    @PostMapping("/items")
    @Operation(summary = "Ajouter un article au panier")
    public ResponseEntity<CartResponse> ajouterArticle(
            @Valid @RequestBody CartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.ajouterArticle(userDetails.getUsername(), request));
    }

    // PUT /api/cart/items/{itemId} — modifier la quantité
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Modifier la quantité d'un article")
    public ResponseEntity<CartResponse> modifierQuantite(
            @PathVariable Long itemId,
            @RequestParam Integer quantite,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.modifierQuantite(userDetails.getUsername(), itemId, quantite));
    }

    // DELETE /api/cart/items/{itemId} — retirer un article
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Retirer un article du panier")
    public ResponseEntity<CartResponse> retirerArticle(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.retirerArticle(userDetails.getUsername(), itemId));
    }

    // POST /api/cart/coupon — appliquer un code promo
    @PostMapping("/coupon")
    @Operation(summary = "Appliquer un code promo au panier")
    public ResponseEntity<CartResponse> appliquerCoupon(
            @RequestParam String code,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.appliquerCoupon(userDetails.getUsername(), code));
    }

    // DELETE /api/cart/coupon — retirer le code promo
    @DeleteMapping("/coupon")
    @Operation(summary = "Retirer le code promo du panier")
    public ResponseEntity<CartResponse> retirerCoupon(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.retirerCoupon(userDetails.getUsername()));
    }
}
