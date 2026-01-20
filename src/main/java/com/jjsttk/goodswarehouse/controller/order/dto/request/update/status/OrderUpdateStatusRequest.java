package com.jjsttk.goodswarehouse.controller.order.dto.request.update.status;

import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "Order status to change")
public record OrderUpdateStatusRequest(

        @Schema(
                description = "Order status",
                example = "DONE"
        )
        @NotNull(message = "Status cannot be null")
        OrderStatus status
) {
}
