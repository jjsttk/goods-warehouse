package com.jjsttk.goodswarehouse.mapper.order.spring.converter;

import com.jjsttk.goodswarehouse.controller.order.dto.response.product.GetOrderProductResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseProductInOrderResponse;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class BaseProductInOrderResponseToGetOrderProductResponseConverter
        implements Converter<BaseProductInOrderResponse, GetOrderProductResponse> {

    @Override
    public GetOrderProductResponse convert(BaseProductInOrderResponse source) {
        return GetOrderProductResponse.builder()
                .productId(source.productId())
                .name(source.name())
                .price(source.price())
                .quantity(source.quantity())
                .build();
    }
}
