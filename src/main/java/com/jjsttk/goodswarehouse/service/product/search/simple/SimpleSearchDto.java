package com.jjsttk.goodswarehouse.service.product.search.simple;

import com.jjsttk.goodswarehouse.controller.request.validation.NotBlankOrNull;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Schema(description = "Filter dto for searching products by name, price, quantity, and pagination parameters")
@Builder
public record SimpleSearchDto(

        @Schema(
                description = "Product name for *iLike* search",
                example = "Laptop",
                maxLength = 50,
                nullable = true
        )
        @Nullable
        @NotBlankOrNull(message = "Name must not be blank")
        @Size(max = 50, message = "Name must be no longer than 50 characters")
        String name,


        @Schema(
                description = "Product price",
                example = "1299.99",
                minimum = "0.01",
                maximum = "99999999.99",
                nullable = true
        )
        @Nullable
        @Positive
        @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits before decimal and 2 after")
        BigDecimal price,


        @Schema(
                description = "Quantity available in stock",
                example = "100.1",
                minimum = "0",
                maximum = "99999999.999",
                nullable = true
        )
        @Nullable
        @PositiveOrZero(message = "Quantity must be positive or zero")
        @Digits(integer = 9, fraction = 3, message = "Quantity must have up to 9 digits before decimal and 3 after")
        BigDecimal quantity,


        @Schema(
                description = "Page number. Zero based",
                example = "5",
                minimum = "0"
        )
        @NotNull(message = "Page must be not null")
        @PositiveOrZero(message = "Page must be positive or zero")
        Integer page,


        @Schema(
                description = "Products on page (limit)",
                example = "20",
                minimum = "1"
        )
        @NotNull(message = "Size must be not null")
        @Positive(message = "Size must be positive")
        Integer size
) {
}
