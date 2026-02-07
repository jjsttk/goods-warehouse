package com.jjsttk.goodswarehouse.mapper.order.spring.converter;

import com.jjsttk.goodswarehouse.service.order.dto.response.BaseProductInOrderResponse;
import com.jjsttk.goodswarehouse.persistence.repository.projections.OrderProductSummaryProjection;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class OrderProductProjectionToBaseProductInOrderResponseConverter
        implements Converter<OrderProductSummaryProjection, BaseProductInOrderResponse> {

    @Override
    public BaseProductInOrderResponse convert(OrderProductSummaryProjection source) {
        return BaseProductInOrderResponse.builder()
                .productId(source.productId())
                .name(source.name())
                .price(source.price())
                .quantity(source.quantity())
                .build();
    }
}
