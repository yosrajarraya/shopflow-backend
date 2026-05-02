package com.shopflow.controller;

import com.shopflow.dto.request.LoginRequest;
import com.shopflow.dto.request.RegisterRequest;
import com.shopflow.dto.response.AuthResponse;
import com.shopflow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentification", description = "Inscription, connexion et gestion des tokens")
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register — inscription client
    @PostMapping("/register")
    @Operation(summary = "Inscrire un client")
    public ResponseEntity<AuthResponse> inscrireClient(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.inscrireClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/auth/register/seller — inscription vendeur
    @PostMapping("/register/seller")
    @Operation(summary = "Inscrire un vendeur")
    public ResponseEntity<AuthResponse> inscrireVendeur(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.inscrireVendeur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /api/auth/login — connexion
    @PostMapping("/login")
    @Operation(summary = "Se connecter (retourne access + refresh token)")
    public ResponseEntity<AuthResponse> connecter(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.connecter(request));
    }

    // POST /api/auth/refresh — renouveler le token
    @PostMapping("/refresh")
    @Operation(summary = "Renouveler l'access token avec le refresh token")
    public ResponseEntity<AuthResponse> rafraichir(@RequestParam String refreshToken) {
        return ResponseEntity.ok(authService.rafraichirToken(refreshToken));
    }

    // POST /api/auth/logout — déconnexion (côté client, supprimer les tokens)
    @PostMapping("/logout")
    @Operation(summary = "Déconnexion (invalider le token côté client)")
    public ResponseEntity<String> deconnecter() {
        // Avec JWT stateless, la déconnexion se fait côté client en supprimant le token
        return ResponseEntity.ok("Déconnexion réussie");
    }
}
