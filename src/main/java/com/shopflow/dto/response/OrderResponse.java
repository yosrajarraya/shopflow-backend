package com.shopflow.dto.response;

import com.shopflow.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String numeroCommande;
    private OrderStatus statut;
    private String adresseLivraison;
    private String notes;
    private Double sousTotal;
    private Double fraisLivraison;
    private Double totalTTC;
    private LocalDateTime dateCommande;
    private boolean isNew;

    // Infos client
    private Long customerId;
    private String customerNom;

    private List<OrderItemResponse> lignes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productNom;
        private String productImage;
        private Integer quantite;
        private Double prixUnitaire;
        private Double sousTotal;
        private String variantInfo;
    }
}
