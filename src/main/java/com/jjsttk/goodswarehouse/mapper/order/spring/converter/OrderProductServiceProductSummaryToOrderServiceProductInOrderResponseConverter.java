package com.jjsttk.goodswarehouse.mapper.order.spring.converter;

import com.jjsttk.goodswarehouse.service.order.dto.response.OrderServiceProductInOrderResponse;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceProductSummary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class OrderProductServiceProductSummaryToOrderServiceProductInOrderResponseConverter
        implements Converter<OrderProductServiceProductSummary, OrderServiceProductInOrderResponse> {

    @Override
    public OrderServiceProductInOrderResponse convert(OrderProductServiceProductSummary source) {
        return OrderServiceProductInOrderResponse.builder()
                .productId(source.productId())
                .name(source.name())
                .price(source.price())
                .quantity(source.quantity())
                .build();
    }
}
