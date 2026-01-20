package com.jjsttk.goodswarehouse.controller.order.dto.request.create;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.product.OrderProductCreateRequest;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "Controller request for creating a order")
public record OrderCreateRequest(
        @Schema(
                description = "Delivery address",
                example = "123456, г. Самара, ул. Победы 90, 217",
                maxLength = 150
        )
        @NotBlank(message = "Delivery address must not be blank")
        @Size(max = 150, message = "Delivery address must be no longer than 150 characters")
        String deliveryAddress,

        @ArraySchema(
                schema = @Schema(implementation = OrderProductCreateRequest.class),
                minItems = 1,
                arraySchema = @Schema(
                        description = "List of products wanted to order",
                        example = """
                                [
                                  {
                                    "id": "550e8400-e29b-41d4-a716-446655440000",
                                    "quantity": 2.123
                                  }
                                ]
                                """
                )
        )
        @NotNull(message = "The product list cannot be null")
        @Size(min = 1, message = "The product list must contain at least 1 product to order")
        @Valid
        List<OrderProductCreateRequest> products
) {
}
