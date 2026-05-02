package com.shopflow.controller;

import com.shopflow.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Statistiques et tableaux de bord")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    // GET /api/dashboard/admin — stats globales (ADMIN)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Dashboard Admin : CA, top produits, commandes récentes")
    public ResponseEntity<Map<String, Object>> dashboardAdmin() {
        return ResponseEntity.ok(dashboardService.dashboardAdmin());
    }

    // GET /api/dashboard/seller — stats du vendeur connecté (SELLER)
    @GetMapping("/seller")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Dashboard Vendeur : commandes en attente, alertes stock")
    public ResponseEntity<Map<String, Object>> dashboardVendeur(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.dashboardVendeur(userDetails.getUsername()));
    }

    // GET /api/dashboard/customer — stats du client connecté (CUSTOMER)
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Dashboard Client : mes commandes, historique")
    public ResponseEntity<Map<String, Object>> dashboardCustomer(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dashboardService.dashboardCustomer(userDetails.getUsername()));
    }
}
