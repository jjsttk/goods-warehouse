package com.jjsttk.goodswarehouse.controller.request;





import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jjsttk.goodswarehouse.configuration.CategoryDeserializer;
import com.jjsttk.goodswarehouse.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating an existing product."
                + " All fields are optional - only provided fields will be updated."
                + " Validation is performed in the service layer.")
public final class UpdateProductRequest {

    @Schema(description = "Product name. Max 50 characters. Validated in service",
            example = "iPhone 16 Pro",
            maxLength = 50,
            nullable = true)
    @Nullable
    private String name;

    @Schema(description = "Unique article (SKU). Validated for uniqueness in service",
            example = "APL-IP16PRO-256GB",
            maxLength = 100,
            nullable = true)
    @Nullable
    private String article;

    @Schema(description = "Detailed product description",
            example = "Latest Apple smartphone with A18 Pro chip",
            nullable = true)
    @Nullable
    private String description;

    @Schema(description = "Product category. Supported values: ELECTRONICS, CLOTHING, BOOKS, FOOD",
            example = "ELECTRONICS",
            allowableValues = {"ELECTRONICS", "FOOD", "CLOTHING", "TOYS", "BOOKS",
                    "FURNITURE", "BEAUTY", "SPORTS", "OFFICE", "PETS"},
            nullable = true)
    @Nullable
    @JsonDeserialize(using = CategoryDeserializer.class)
    private Category category;

    @Schema(description = "Product price. Must be positive. Validated in service",
            example = "1199.99",
            nullable = true)
    @Nullable
    private BigDecimal price;

    @Schema(description = "Available quantity in stock. Must be zero or positive. Validated in service",
            example = "40.500",
            nullable = true)
    @Nullable
    private BigDecimal quantity;
}
