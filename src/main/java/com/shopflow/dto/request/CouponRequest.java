package com.shopflow.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CouponRequest {
    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotBlank(message = "Le type est obligatoire")
    private String type; // PERCENT or FIXED

    @NotNull @Positive
    private Double valeur;

    // Accepte "yyyy-MM-dd" depuis le frontend (input type="date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateExpiration;

    @Positive
    private Integer usagesMax = 100;

    private boolean actif = true;
}
