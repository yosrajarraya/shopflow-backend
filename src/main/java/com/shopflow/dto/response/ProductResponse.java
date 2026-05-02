package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String nom;
    private String description;
    private Double prix;
    private Double prixPromo;
    private Double pourcentageRemise;
    private Integer stock;
    private boolean actif;
    private LocalDateTime dateCreation;
    private Integer nombreVentes;

    // Infos du vendeur
    private Long sellerId;
    private String sellerNom;
    private String sellerBoutique;

    private Set<CategoryResponse> categories;
    private List<String> images;
    private List<VariantResponse> variantes;
    private List<ReviewResponse> avis;
    private Double noteMoyenne;

    // DTO imbriqué pour les variantes
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantResponse {
        private Long id;
        private String attribut;
        private String valeur;
        private Integer stockSupplementaire;
        private Double prixDelta;
    }
}
