package com.jjsttk.goodswarehouse.mapper.order.spring;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.mapper.order.OrderControllerConverter;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.order.price.dto.response.OrderPriceExchangeServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class ConversionServiceOrderControllerMapper implements OrderControllerConverter {
    private final ConversionService conversionService;

    @Override
    public OrderServiceCreateCommand toServiceCommand(OrderCreateRequest createRequest) {
        return conversionService.convert(createRequest, OrderServiceCreateCommand.class);
    }

    @Override
    public OrderServiceUpdateCommand toServiceCommand(UUID orderId, Collection<OrderProductUpdateRequest> request) {
        return OrderServiceUpdateCommand.builder()
                .orderId(orderId)
                .productQuantities(request.stream().collect(Collectors.toMap(
                        OrderProductUpdateRequest::id,
                        OrderProductUpdateRequest::quantity,
                        BigDecimal::add
                )))
                .build();
    }

    @Override
    public GetOrderResponse toResponse(OrderPriceExchangeServiceResponse serviceResponse) {
        return conversionService.convert(serviceResponse, GetOrderResponse.class);
    }
}
