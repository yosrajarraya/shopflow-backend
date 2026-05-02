package com.shopflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Configuration Swagger UI accessible sur http://localhost:8080/swagger-ui
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "ShopFlow API",
        version = "1.0",
        description = "API REST de la boutique en ligne ShopFlow — Projet Dr. Ing. Ghada Feki"
    )
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {
    // Spring Boot + Springdoc auto-configure tout, pas besoin de beans supplémentaires
}
