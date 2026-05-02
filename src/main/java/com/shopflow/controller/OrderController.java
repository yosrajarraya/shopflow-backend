package com.shopflow.controller;

import com.shopflow.dto.request.OrderRequest;
import com.shopflow.dto.response.OrderResponse;
import com.shopflow.enums.OrderStatus;
import com.shopflow.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Commandes", description = "Gestion des commandes")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders — passer une commande (CUSTOMER)
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Passer une commande depuis le panier")
    public ResponseEntity<OrderResponse> passerCommande(
            @Valid @RequestBody OrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponse response = orderService.passerCommande(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/orders/my — mes commandes (CUSTOMER)
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Voir mes commandes")
    public ResponseEntity<List<OrderResponse>> mesCommandes(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.mesCommandes(userDetails.getUsername()));
    }

    // GET /api/orders/seller/my — mes commandes (SELLER) - basées sur produits du vendeur
    @GetMapping("/seller/my")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Voir mes commandes (vendeur)")
    public ResponseEntity<List<OrderResponse>> mesCommandesVendeur(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.mesCommandesVendeur(userDetails.getUsername()));
    }

    // GET /api/orders/{id} — détail d'une commande
    @GetMapping("/{id}")
    @Operation(summary = "Voir le détail d'une commande")
    public ResponseEntity<OrderResponse> voirCommande(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.voirCommande(id, userDetails.getUsername()));
    }

    // PUT /api/orders/{id}/status — mettre à jour le statut (SELLER ou ADMIN)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour le statut d'une commande")
    public ResponseEntity<OrderResponse> mettreAJourStatut(
            @PathVariable Long id,
            @RequestParam OrderStatus statut,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.mettreAJourStatut(id, statut, userDetails.getUsername()));
    }

    // PUT /api/orders/{id}/seller-decision?action=ACCEPT|REFUSE — décision vendeur
    @PutMapping("/{id}/seller-decision")
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    @Operation(summary = "Accepter ou refuser une commande côté vendeur")
    public ResponseEntity<OrderResponse> decisionCommandeVendeur(
            @PathVariable Long id,
            @RequestParam String action,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.decisionCommandeVendeur(id, action, userDetails.getUsername()));
    }

    // PUT /api/orders/{id}/cancel — annuler une commande (CUSTOMER)
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Annuler une commande (seulement si PENDING ou PAID)")
    public ResponseEntity<OrderResponse> annulerCommande(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(orderService.annulerCommande(id, userDetails.getUsername()));
    }

    // GET /api/orders — toutes les commandes (ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toutes les commandes (Admin)")
    public ResponseEntity<List<OrderResponse>> toutesLesCommandes() {
        return ResponseEntity.ok(orderService.toutesLesCommandes());
    }
}
