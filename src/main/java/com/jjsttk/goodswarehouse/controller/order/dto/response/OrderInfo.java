package com.jjsttk.goodswarehouse.controller.order.dto.response;

import com.jjsttk.goodswarehouse.controller.order.dto.response.customer.CustomerInfo;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderInfo(
        UUID id,
        CustomerInfo customer,
        OrderStatus status,
        String deliveryAddress,
        BigDecimal quantity
) {
}
