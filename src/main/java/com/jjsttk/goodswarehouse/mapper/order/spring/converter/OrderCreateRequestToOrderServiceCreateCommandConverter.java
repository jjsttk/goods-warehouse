package com.jjsttk.goodswarehouse.mapper.order.spring.converter;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.create.product.OrderProductCreateRequest;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceCreateCommand;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public final class OrderCreateRequestToOrderServiceCreateCommandConverter
        implements Converter<OrderCreateRequest, OrderServiceCreateCommand> {

    @Override
    public OrderServiceCreateCommand convert(OrderCreateRequest source) {
        return OrderServiceCreateCommand.builder()
                .deliveryAddress(source.deliveryAddress())
                .productQuantities(source.products().stream().collect(Collectors.toMap(
                        OrderProductCreateRequest::id,
                        OrderProductCreateRequest::quantity,
                        BigDecimal::add
                )))
                .build();
    }
}
