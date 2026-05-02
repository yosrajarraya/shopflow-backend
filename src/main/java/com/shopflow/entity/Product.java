package com.shopflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le vendeur qui possède ce produit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @Column(nullable = false)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double prix;

    // Prix en promotion (null si pas de promo)
    private Double prixPromo;

    @Builder.Default
    private Integer stock = 0;

    @Builder.Default
    private boolean actif = true;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Relation ManyToMany avec Category
    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    // URLs des images du produit
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> images = new ArrayList<>();

    // Variantes du produit (tailles, couleurs...)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductVariant> variantes = new ArrayList<>();

    // Avis des clients
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> avis = new ArrayList<>();

    // Nombre de ventes (pour top-selling)
    @Builder.Default
    private Integer nombreVentes = 0;

    // Calculer le pourcentage de remise
    public Double getPourcentageRemise() {
        if (prixPromo != null && prix > 0) {
            return ((prix - prixPromo) / prix) * 100;
        }
        return null;
    }
}
