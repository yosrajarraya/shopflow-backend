package com.shopflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String description;

    // ID de la catégorie parente (optionnel)
    private Long parentId;
}
