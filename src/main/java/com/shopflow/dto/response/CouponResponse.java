package com.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CouponResponse {
    private Long id;
    private String code;
    private String type; // PERCENT or FIXED
    private Double valeur;
    private LocalDateTime dateExpiration;
    private Integer usagesMax;
    private Integer usagesActuels;
    private boolean actif;
}
