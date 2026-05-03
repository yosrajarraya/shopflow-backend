package com.shopflow.controller;

import com.shopflow.dto.request.CouponRequest;
import com.shopflow.dto.response.CouponResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Gestion des codes promotionnels")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les coupons (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<CouponResponse>> listerCoupons() {
        return ResponseEntity.ok(couponService.listerCoupons());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un coupon (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CouponResponse> creerCoupon(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.creerCoupon(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un coupon (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CouponResponse> modifierCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.modifierCoupon(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un coupon (Admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> supprimerCoupon(@PathVariable Long id) {
        couponService.supprimerCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate/{code}")
    @Operation(summary = "Valider un code promo (public)")
    public ResponseEntity<CouponResponse> validerCoupon(@PathVariable String code) {
        return ResponseEntity.ok(couponService.validerCoupon(code));
    }
}
