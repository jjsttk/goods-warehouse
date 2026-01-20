package com.jjsttk.goodswarehouse.mapper.order.spring;

import com.jjsttk.goodswarehouse.mapper.order.OrderServiceConverter;
import com.jjsttk.goodswarehouse.mapper.util.converter.ReferenceConverter;
import com.jjsttk.goodswarehouse.persistence.entity.customer.CustomerEntity;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseProductInOrderResponse;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductResponseContainer;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

@Component
@RequiredArgsConstructor
public final class ConversionServiceOrderServiceMapper implements OrderServiceConverter {
    private final ConversionService conversionService;
    private final ReferenceConverter entityConverter;

    @Override
    public OrderEntity toEntity(
            Long customerId,
            String deliveryAddress
    ) {
        return OrderEntity.builder()
                .customer(entityConverter.toEntity(customerId, CustomerEntity.class))
                .deliveryAddress(deliveryAddress)
                .build();
    }

    @Override
    public BaseOrderResponse toResponse(
            UUID orderId,
            OrderProductResponseContainer<OrderProductProjection> serviceResponse
    ) {
        return serviceResponse.orderProducts().stream()
                .collect(Collectors.teeing(

                        Collectors.mapping(
                                summary ->
                                        Objects.requireNonNull(
                                                conversionService.convert(
                                                        summary,
                                                        BaseProductInOrderResponse.class
                                                )
                                        ),
                                toList()
                        ),

                        Collectors.mapping(
                                summary -> summary.price().multiply(summary.quantity()),
                                reducing(BigDecimal.ZERO, BigDecimal::add)
                        ),

                        (products, totalPrice) -> BaseOrderResponse.builder()
                                .orderId(orderId)
                                .products(products)
                                .totalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP))
                                .build()
                ));
    }
}
