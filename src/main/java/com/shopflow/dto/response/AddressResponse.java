package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String rue;
    private String ville;
    private String codePostal;
    private String pays;
    private boolean principal;
}
