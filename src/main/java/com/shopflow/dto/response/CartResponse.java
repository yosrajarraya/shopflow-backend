package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private List<CartItemResponse> lignes;
    private String codeCoupon;
    private Double sousTotal;
    private Double remise;
    private Double fraisLivraison;
    private Double totalTTC;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productNom;
        private String productImage;
        private Double prixUnitaire;
        private Integer quantite;
        private Double sousTotal;
        // Variante choisie
        private Long variantId;
        private String variantInfo;
    }
}
