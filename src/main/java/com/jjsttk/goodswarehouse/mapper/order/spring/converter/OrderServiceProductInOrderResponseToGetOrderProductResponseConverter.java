package com.jjsttk.goodswarehouse.mapper.order.spring.converter;

import com.jjsttk.goodswarehouse.controller.order.dto.response.product.GetOrderProductResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.OrderServiceProductInOrderResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class OrderServiceProductInOrderResponseToGetOrderProductResponseConverter
        implements Converter<OrderServiceProductInOrderResponse, GetOrderProductResponse> {

    @Override
    public GetOrderProductResponse convert(OrderServiceProductInOrderResponse source) {
        return GetOrderProductResponse.builder()
                .productId(source.productId())
                .name(source.name())
                .price(source.price())
                .quantity(source.quantity())
                .build();
    }
}
