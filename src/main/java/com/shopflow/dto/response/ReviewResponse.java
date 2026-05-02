package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private Long customerId;
    private String customerNom;
    private Integer note;
    private String commentaire;
    private LocalDateTime dateCreation;
    private boolean approuve;
}
