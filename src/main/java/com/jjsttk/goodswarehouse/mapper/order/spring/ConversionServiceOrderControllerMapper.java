package com.jjsttk.goodswarehouse.mapper.order.spring;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.mapper.order.OrderControllerConverter;
import com.jjsttk.goodswarehouse.service.order.dto.command.CreateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class ConversionServiceOrderControllerMapper implements OrderControllerConverter {
    private final ConversionService conversionService;

    @Override
    public CreateOrderCommandInfo toServiceCommand(OrderCreateRequest createRequest) {
        return Objects.requireNonNull(conversionService.convert(createRequest, CreateOrderCommandInfo.class));
    }

    @Override
    public UpdateOrderCommandInfo toServiceCommand(UUID orderId, Collection<OrderProductUpdateRequest> request) {
        return UpdateOrderCommandInfo.builder()
                .orderId(orderId)
                .productQuantities(request.stream().collect(Collectors.toMap(
                        OrderProductUpdateRequest::id,
                        OrderProductUpdateRequest::quantity,
                        BigDecimal::add
                )))
                .build();
    }

    @Override
    public GetOrderResponse toResponse(BaseOrderResponse serviceResponse) {
        return Objects.requireNonNull(conversionService.convert(serviceResponse, GetOrderResponse.class));
    }
}
