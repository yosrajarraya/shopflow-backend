package com.shopflow.dto.request;

import com.shopflow.enums.CouponType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponRequest {

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotNull(message = "Le type est obligatoire")
    private CouponType type;

    @NotNull(message = "La valeur est obligatoire")
    private Double valeur;

    private LocalDateTime dateExpiration;
    private Integer usagesMax = 100;
}
