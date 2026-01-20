package com.jjsttk.goodswarehouse.mapper.order.spring.converter;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.create.product.OrderProductCreateRequest;
import com.jjsttk.goodswarehouse.service.order.dto.command.CreateOrderCommandInfo;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public final class OrderCreateRequestToCreateOrderCommandInfoConverter
        implements Converter<OrderCreateRequest, CreateOrderCommandInfo> {

    @Override
    public CreateOrderCommandInfo convert(OrderCreateRequest source) {
        return CreateOrderCommandInfo.builder()
                .deliveryAddress(source.deliveryAddress())
                .productQuantities(source.products().stream().collect(Collectors.toMap(
                        OrderProductCreateRequest::id,
                        OrderProductCreateRequest::quantity,
                        BigDecimal::add
                )))
                .build();
    }
}
