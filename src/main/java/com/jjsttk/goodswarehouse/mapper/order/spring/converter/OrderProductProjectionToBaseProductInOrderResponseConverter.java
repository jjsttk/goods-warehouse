package com.jjsttk.goodswarehouse.mapper.order.spring.converter;

import com.jjsttk.goodswarehouse.service.order.dto.response.BaseProductInOrderResponse;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class OrderProductProjectionToBaseProductInOrderResponseConverter
        implements Converter<OrderProductProjection, BaseProductInOrderResponse> {

    @Override
    public BaseProductInOrderResponse convert(OrderProductProjection source) {
        return BaseProductInOrderResponse.builder()
                .productId(source.productId())
                .name(source.name())
                .price(source.price())
                .quantity(source.quantity())
                .build();
    }
}
