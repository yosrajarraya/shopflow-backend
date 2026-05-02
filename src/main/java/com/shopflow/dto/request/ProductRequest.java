package com.shopflow.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ProductRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @Min(value = 0, message = "Le prix doit être positif")
    private Double prix;

    private Double prixPromo;

    @Min(value = 0, message = "Le stock doit être positif")
    private Integer stock = 0;

    // IDs des catégories
    private Set<Long> categoryIds;

    // URLs des images
    private List<String> images;
}
