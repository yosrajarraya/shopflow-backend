package com.shopflow.controller;

import com.shopflow.dto.request.AddressRequest;
import com.shopflow.dto.response.AddressResponse;
import com.shopflow.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Adresses", description = "Gestion des adresses de livraison")
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Mes adresses de livraison")
    public ResponseEntity<List<AddressResponse>> mesAdresses(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.mesAdresses(userDetails.getUsername()));
    }

    @PostMapping
    @Operation(summary = "Ajouter une adresse")
    public ResponseEntity<AddressResponse> ajouterAdresse(
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.ajouterAdresse(userDetails.getUsername(), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une adresse")
    public ResponseEntity<AddressResponse> modifierAdresse(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.modifierAdresse(id, userDetails.getUsername(), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une adresse")
    public ResponseEntity<Void> supprimerAdresse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        addressService.supprimerAdresse(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/principal")
    @Operation(summary = "Définir comme adresse principale")
    public ResponseEntity<AddressResponse> definirPrincipale(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(addressService.definirPrincipale(id, userDetails.getUsername()));
    }
}
