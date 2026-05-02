package com.shopflow.entity;

import com.shopflow.enums.CouponType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private CouponType type;

    // Valeur de la réduction (% ou montant fixe)
    private Double valeur;

    private LocalDateTime dateExpiration;

    @Builder.Default
    private Integer usagesMax = 100;

    @Builder.Default
    private Integer usagesActuels = 0;

    @Builder.Default
    private boolean actif = true;
}
