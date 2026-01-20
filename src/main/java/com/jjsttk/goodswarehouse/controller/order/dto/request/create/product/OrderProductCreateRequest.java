package com.jjsttk.goodswarehouse.controller.order.dto.request.create.product;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Schema(description = "Data of product picked to order")
public record OrderProductCreateRequest(
        @NotNull(message = "Id cannot be null")
        UUID id,

        @Schema(
                description = "Quantity to order",
                example = "100",
                minimum = "0",
                maximum = "99999999.999"
        )
        @NotNull(message = "Quantity cannot be null")
        @Positive(message = "Quantity for ordering product must be positive")
        @Digits(integer = 9, fraction = 3, message = "Quantity must have up to 9 digits before decimal and 3 after")
        BigDecimal quantity
) {
}
