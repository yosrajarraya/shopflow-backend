package com.shopflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {
    @NotBlank(message = "La rue est obligatoire")
    private String rue;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @NotBlank(message = "Le code postal est obligatoire")
    private String codePostal;

    @NotBlank(message = "Le pays est obligatoire")
    private String pays;

    private boolean principal = false;
}
