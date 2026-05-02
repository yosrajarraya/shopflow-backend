package com.shopflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_variants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Ex: "Taille", "Couleur"
    private String attribut;

    // Ex: "M", "Rouge"
    private String valeur;

    // Stock supplémentaire pour cette variante
    @Builder.Default
    private Integer stockSupplementaire = 0;

    // Prix en plus par rapport au produit de base (peut être négatif)
    @Builder.Default
    private Double prixDelta = 0.0;
}
