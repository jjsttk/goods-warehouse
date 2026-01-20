package com.jjsttk.goodswarehouse.controller.order.dto.response;

import com.jjsttk.goodswarehouse.controller.order.dto.response.product.GetOrderProductResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "Detailed order information")
@Builder
public record GetOrderResponse(
        @Schema(
                description = "Unique identifier of the order",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @ArraySchema(
                arraySchema = @Schema(description = "List of products in current order"),
                schema = @Schema(implementation = GetOrderProductResponse.class)
        )
        List<GetOrderProductResponse> products,

        @Schema(
                description = "The price of the order at the time of placement"
        )
        BigDecimal totalPrice
) {
}
