package com.jjsttk.goodswarehouse.mapper.order.spring;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.controller.order.dto.response.product.GetOrderProductResponse;
import com.jjsttk.goodswarehouse.mapper.order.OrderControllerConverter;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeRate;
import com.jjsttk.goodswarehouse.service.order.dto.command.CreateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.command.UpdateOrderCommandInfo;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseProductInOrderResponse;
import com.jjsttk.goodswarehouse.shared.util.price.PriceConverter;
import com.jjsttk.goodswarehouse.shared.util.price.dto.request.PriceConverterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
    public GetOrderResponse toResponse(
            BaseOrderResponse serviceResponse,
            ExchangeRate exchangeRate
    ) {

        var totalPrice = BigDecimal.ZERO;
        var convertedProducts = new ArrayList<GetOrderProductResponse>(serviceResponse.products().size());

        for (var src : serviceResponse.products()) {
            var converted = elementToResponse(src, exchangeRate.rate());
            convertedProducts.add(converted);

            var itemTotal = converted.price().multiply(converted.quantity());
            totalPrice = totalPrice.add(itemTotal);
        }

        return GetOrderResponse.builder()
                .id(serviceResponse.orderId())
                .products(convertedProducts)
                .totalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP))
                .currency(exchangeRate.currency())
                .build();
    }

    private GetOrderProductResponse elementToResponse(
            BaseProductInOrderResponse src,
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
