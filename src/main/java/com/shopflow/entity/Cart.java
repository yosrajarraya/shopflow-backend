package com.shopflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Chaque client a un seul panier
    @OneToOne
    @JoinColumn(name = "customer_id", unique = true)
    private User customer;

    // Articles dans le panier
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> lignes = new ArrayList<>();

    // Code promo appliqué
    private String codeCoupon;

    @Builder.Default
    private LocalDateTime dateModification = LocalDateTime.now();
}
