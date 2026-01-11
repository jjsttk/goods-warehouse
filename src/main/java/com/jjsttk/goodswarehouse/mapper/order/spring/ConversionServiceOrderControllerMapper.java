package com.jjsttk.goodswarehouse.mapper.order.spring;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.controller.order.dto.response.product.GetOrderProductResponse;
import com.jjsttk.goodswarehouse.mapper.order.OrderControllerConverter;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeRate;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderServiceResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.OrderServiceProductInOrderResponse;
import com.jjsttk.goodswarehouse.shared.util.price.PriceConverter;
import com.jjsttk.goodswarehouse.shared.util.price.dto.request.PriceConverterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public GetOrderResponse toResponse(
            BaseOrderServiceResponse serviceResponse,
            ExchangeRate exchangeRate
    ) {
        var convertedProducts = serviceResponse.products().stream()
                .map(it -> elementToResponse(it, exchangeRate.rate()))
                .toList();

        var totalPrice = convertedProducts.stream()
                .map(p -> p.price().multiply(p.quantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return GetOrderResponse.builder()
                .id(serviceResponse.orderId())
                .products(convertedProducts)
                .totalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP))
                .currency(exchangeRate.currency())
                .build();
    }

    protected GetOrderProductResponse elementToResponse(
            OrderServiceProductInOrderResponse src,
            BigDecimal actualCurrencyRate
    ) {
        return GetOrderProductResponse.builder()
                .productId(src.productId())
                .name(src.name())
                .price(PriceConverter.convert(
                        PriceConverterRequest.builder()
                                .sourcePrice(src.price())
                                .actualRate(actualCurrencyRate)
                                .build()
                ))
                .quantity(src.quantity())
                .build();
    }
}
