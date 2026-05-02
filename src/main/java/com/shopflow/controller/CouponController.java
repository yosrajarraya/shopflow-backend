package com.shopflow.controller;

import com.shopflow.dto.request.CouponRequest;
import com.shopflow.entity.Coupon;
import com.shopflow.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Gestion des codes promo")
public class CouponController {

    private final CouponService couponService;

    // POST /api/coupons — créer un coupon (ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un coupon de réduction",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Coupon> creerCoupon(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.creerCoupon(request));
    }

    // PUT /api/coupons/{id} — modifier (ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un coupon",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Coupon> modifierCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.modifierCoupon(id, request));
    }

    // DELETE /api/coupons/{id} — supprimer (ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un coupon",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> supprimerCoupon(@PathVariable Long id) {
        couponService.supprimerCoupon(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/coupons/validate/{code} — vérifier la validité (tous les authentifiés)
    @GetMapping("/validate/{code}")
    @Operation(summary = "Vérifier la validité d'un code promo",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Coupon> validerCoupon(@PathVariable String code) {
        return ResponseEntity.ok(couponService.validerCoupon(code));
    }
}
