package com.jjsttk.goodswarehouse.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jjsttk.goodswarehouse.enums.Category;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Detailed product information")
public record GetProductResponse(

        @Schema(
                description = "Unique identifier of the product",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Product name",
                example = "iPhone 16 Pro"
        )
        String name,

        @Schema(
                description = "Unique article number (SKU)",
                example = "APL-IP16PRO-256-BLK"
        )
        String article,

        @Schema(
                description = "Product description",
                example = "Apple smartphone with 256GB storage in black color"
        )
        String description,

        @Schema(
                description = "Product category",
                example = "ELECTRONICS"
        )
        Category category,

        @Schema(
                description = "Product price",
                example = "1299.99"
        )
        BigDecimal price,

        @Schema(
                description = "Quantity of product available in stock",
                example = "150"
        )
        BigDecimal quantity,

        @Schema(
                description = "Short name of currency",
                example = "EUR"
        )
        PriceCurrency currency,

        @Schema(
                description = "Timestamp with timezone offset when product quantity was last modified",
                example = "2025-08-28T12:45:30+03:00"
        )
        OffsetDateTime lastQuantityModified,

        @Schema(
                description = "Server date when the product was created",
                example = "25-12-2024"
        )
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "dd-MM-yyyy"
        )
        LocalDate createdAt
) {
}
