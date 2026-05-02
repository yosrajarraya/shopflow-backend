package com.shopflow.controller;

import com.shopflow.dto.response.UserResponse;
import com.shopflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - Utilisateurs", description = "Gestion des utilisateurs (Admin uniquement)")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    // GET /api/admin/users — lister tous les utilisateurs
    @GetMapping
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<List<UserResponse>> listerUtilisateurs() {
        return ResponseEntity.ok(userService.listerUtilisateurs());
    }

    // GET /api/admin/users/{id} — détail d'un utilisateur
    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un utilisateur")
    public ResponseEntity<UserResponse> obtenirUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenirUtilisateur(id));
    }

    // PUT /api/admin/users/{id} — modifier un utilisateur
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un utilisateur")
    public ResponseEntity<UserResponse> modifierUtilisateur(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String nom       = body.getOrDefault("nom", "");
        String prenom    = body.getOrDefault("prenom", "");
        String email     = body.getOrDefault("email", "");
        String telephone = body.getOrDefault("telephone", null);
        String role      = body.getOrDefault("role", "CUSTOMER");
        return ResponseEntity.ok(userService.modifierUtilisateur(id, nom, prenom, email, telephone, role));
    }

    // PUT /api/admin/users/{id}/activate — activer un compte
    @PutMapping("/{id}/activate")
    @Operation(summary = "Activer un compte utilisateur")
    public ResponseEntity<UserResponse> activerCompte(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activerCompte(id));
    }

    // PUT /api/admin/users/{id}/deactivate — désactiver un compte
    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Désactiver un compte utilisateur")
    public ResponseEntity<UserResponse> desactiverCompte(@PathVariable Long id) {
        return ResponseEntity.ok(userService.desactiverCompte(id));
    }

    // DELETE /api/admin/users/{id} — supprimer un utilisateur
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        userService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }
}
