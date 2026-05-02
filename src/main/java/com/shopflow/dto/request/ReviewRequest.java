package com.shopflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "L'id du produit est obligatoire")
    private Long productId;

    @NotNull(message = "L'id de la commande est obligatoire")
    private Long orderId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimale est 1")
    @Max(value = 5, message = "La note maximale est 5")
    private Integer note;

    @NotBlank(message = "Le commentaire est obligatoire")
    private String commentaire;
}
