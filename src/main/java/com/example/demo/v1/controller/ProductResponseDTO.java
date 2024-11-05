package com.example.demo.v1.controller;

import com.example.demo.repository.entity.ProductEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Schema(name = "Product Response")
@Builder
public record ProductResponseDTO(
        @Schema(description = "Product id", example = "6ec58140-b159-4a5b-af91-3f976f8ebcb4")
        String productID,
        @Schema(description = "Product name", example = "JVM")
        String name,
        PriceResponse price
) {

    public static ProductResponseDTO entityToResponse(ProductEntity productEntity, String currency) {
        return ProductResponseDTO.builder()
                .productID(productEntity.getProductID())
                .name(productEntity.getName())
                .price(new PriceResponse(currency, productEntity.getPrice()))
                .build();
    }

    @Builder
    public record PriceResponse(
            @Schema(description = "The currency used in the price", example = "USD")
            String currency,

            @Schema(description = "The product value", example = "200.50")
            BigDecimal value
    ) {
        public PriceResponse {
            if (value != null) {
                value = value.setScale(2, RoundingMode.HALF_UP);
            }
        }
    }
}
