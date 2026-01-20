package com.jjsttk.goodswarehouse.controller.order.dto.response.product;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Product in order information")
@Builder
public record GetOrderProductResponse(

        @Schema(
                description = "Unique identifier of the product in order",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID productId,

        @Schema(
                description = "Name of the product in order",
                example = "Potato"
        )
        String name,

        @Schema(
                description = "Quantity of ordered product",
                example = "12.950"
        )
        BigDecimal quantity,

        @Schema(
                description = "Price of product in order",
                example = "11.12"
        )
        BigDecimal price
) {
}
