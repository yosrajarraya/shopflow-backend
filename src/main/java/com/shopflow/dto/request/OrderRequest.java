package com.shopflow.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderRequest {

    @Size(min = 10, message = "L'adresse doit contenir au moins 10 caractères")
    private String adresseLivraison;

    // Optionnel: sélectionner une adresse enregistrée de l'utilisateur
    private Long addressId;

    @Size(max = 500, message = "Les notes ne doivent pas dépasser 500 caractères")
    private String notes;
}
