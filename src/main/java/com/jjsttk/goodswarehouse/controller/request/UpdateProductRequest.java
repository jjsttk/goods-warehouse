package com.jjsttk.goodswarehouse.controller.request;


import com.jjsttk.goodswarehouse.controller.request.validation.NotBlankOrNull;
import com.jjsttk.goodswarehouse.enums.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Controller request for updating an existing product."
        + " All fields are optional - only provided fields will be updated.")
public final class UpdateProductRequest {

    @Schema(
            description = "Product name. Max 50 characters",
            example = "iPhone 16 Pro",
            maxLength = 50,
            nullable = true
    )
    @Nullable
    @NotBlankOrNull(message = "Name must not be blank")
    @Size(max = 50, message = "Name must be no longer than 50 characters")
    private String name;

    @Schema(
            description = "Unique product article code. Must not duplicate existing articles",
            example = "APL-IPH16PRO-BLK",
            maxLength = 100,
            nullable = true
    )
    @Nullable
    @NotBlankOrNull
    @Size(max = 100, message = "Article must be no longer than 100 characters")
    private String article;

    @Schema(
            description = "Detailed product description. Optional field",
            example = "Latest flagship Apple smartphone with titanium frame",
            nullable = true
    )
    @Nullable
    private String description;

    @Schema(
            description = "Product category. Supported values: ELECTRONICS, CLOTHING, BOOKS, FOOD",
            example = "ELECTRONICS",
            allowableValues = {
                    "ELECTRONICS", "FOOD", "CLOTHING", "TOYS", "BOOKS",
                    "FURNITURE", "BEAUTY", "SPORTS", "OFFICE", "PETS"
            },
            nullable = true
    )
    @Nullable
    private Category category;

    @Schema(
            description = "Product price",
            example = "1299.99",
            minimum = "0.01",
            maximum = "99999999.99",
            nullable = true
    )
    @Nullable
    @Positive(message = "Price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits before decimal and 2 after")
    private BigDecimal price;

    @Schema(
            description = "Quantity available in stock",
            example = "100",
            minimum = "0",
            maximum = "99999999.999",
            nullable = true
    )
    @Nullable
    @PositiveOrZero(message = "Quantity must be positive or zero")
    @Digits(integer = 9, fraction = 3, message = "Quantity must have up to 9 digits before decimal and 3 after")
    private BigDecimal quantity;
}
