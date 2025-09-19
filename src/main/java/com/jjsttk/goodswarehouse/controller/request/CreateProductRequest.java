package com.jjsttk.goodswarehouse.controller.request;

import com.jjsttk.goodswarehouse.enums.Category;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * Request object for creating a new product in the warehouse system.
 * All fields except description are required.
 * Price must be positive, quantity can be zero or more.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "Controller request for creating a new product"
)
public final class CreateProductRequest {
    @Schema(
            description = "Product name. Max 50 characters",
            example = "iPhone 16 Pro",
            maxLength = 50
    )
    @NotBlank(message = "Name must not be blank")
    @Size(max = 50, message = "Name must be no longer than 50 characters")
    private String name;

    @Schema(
            description = "Unique product article code. Must not duplicate existing articles",
            example = "APL-IPH16PRO-BLK",
            maxLength = 100
    )
    @NotBlank(message = "Article must not be blank")
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
            }
            )
    @NotNull(message = "Category must not be null")
    private Category category;

    @Schema(
            description = "Product price",
            example = "1299.99",
            minimum = "0.01",
            maximum = "99999999.99"
    )
    @NotNull(message = "Price must not be null")
    @Positive(message = "Price must be positive")
    @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits before decimal and 2 after")
    private BigDecimal price;

    @Schema(
            description = "Quantity available in stock",
            example = "100",
            minimum = "0",
            maximum = "99999999.999"
    )
    @NotNull(message = "Quantity must not be null")
    @PositiveOrZero(message = "Quantity must be positive or zero")
    @Digits(integer = 9, fraction = 3, message = "Quantity must have up to 9 digits before decimal and 3 after")
    private BigDecimal quantity;
}
