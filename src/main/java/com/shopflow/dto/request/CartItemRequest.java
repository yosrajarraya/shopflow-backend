package com.shopflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotNull(message = "L'id du produit est obligatoire")
    private Long productId;

    // Optionnel si le produit a des variantes
    private Long variantId;

    @NotNull(message = "La quantité est obligatoire")
    @Min(value = 1, message = "La quantité minimale est 1")
    private Integer quantite;
}
