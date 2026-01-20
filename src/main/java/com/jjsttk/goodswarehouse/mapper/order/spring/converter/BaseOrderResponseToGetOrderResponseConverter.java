package com.jjsttk.goodswarehouse.mapper.order.spring.converter;

import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class BaseOrderResponseToGetOrderResponseConverter
        implements Converter<BaseOrderResponse, GetOrderResponse> {

    private final BaseProductInOrderResponseToGetOrderProductResponseConverter converter;

    @Override
    public GetOrderResponse convert(BaseOrderResponse source) {
        var products = source.products();

        return products.stream()
                .collect(Collectors.teeing(

                        Collectors.reducing(
                                BigDecimal.ZERO,
                                it -> it.price().multiply(it.quantity()),
                                BigDecimal::add
                        ),

                        Collectors.mapping(
                                converter::convert,
                                Collectors.toList()
                        ),

                        (total, convertedProducts) -> GetOrderResponse.builder()
                                .id(source.orderId())
                                .products(convertedProducts)
                                .totalPrice(total.setScale(2, RoundingMode.HALF_UP))
                                .build()
                ));
    }
}
