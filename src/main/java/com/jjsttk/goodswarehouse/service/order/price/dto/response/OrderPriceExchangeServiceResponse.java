package com.jjsttk.goodswarehouse.service.order.price.dto.response;

import com.jjsttk.goodswarehouse.service.order.dto.response.OrderServiceProductInOrderResponse;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderPriceExchangeServiceResponse(
        UUID orderId,
        List<OrderServiceProductInOrderResponse> products,
        BigDecimal totalPrice,
        PriceCurrency currency
) {
}
