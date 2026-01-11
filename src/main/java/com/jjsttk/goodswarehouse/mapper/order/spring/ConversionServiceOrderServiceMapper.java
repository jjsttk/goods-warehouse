package com.jjsttk.goodswarehouse.mapper.order.spring;

import com.jjsttk.goodswarehouse.mapper.order.OrderServiceConverter;
import com.jjsttk.goodswarehouse.mapper.util.converter.ReferenceConverter;
import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderServiceResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.OrderServiceProductInOrderResponse;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceProductSummary;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductServiceResponseContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public final class ConversionServiceOrderServiceMapper implements OrderServiceConverter {
    private final ConversionService conversionService;
    private final ReferenceConverter entityConverter;

    @Override
    public OrderEntity toEntity(
            @NonNull Long customerId,
            @NonNull String deliveryAddress
    ) {
        return OrderEntity.builder()
                .customer(entityConverter.toEntity(customerId, CustomerEntity.class))
                .deliveryAddress(deliveryAddress)
                .build();
    }

    @Override
    public BaseOrderServiceResponse toResponse(
            @NonNull UUID orderId,
            @NonNull OrderProductServiceResponseContainer<OrderProductServiceProductSummary> serviceResponse
    ) {
        return BaseOrderServiceResponse.builder()
                .orderId(orderId)
                .products(serviceResponse.orderProducts().stream()
                        .map(it -> conversionService.convert(it, OrderServiceProductInOrderResponse.class))
                        .toList())
                .build();
    }
}
