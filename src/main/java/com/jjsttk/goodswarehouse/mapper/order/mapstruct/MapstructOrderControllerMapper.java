package com.jjsttk.goodswarehouse.mapper.order.mapstruct;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.create.product.OrderProductCreateRequest;
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
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(
        imports = {PriceConverterRequest.class, PriceConverter.class},
        uses = MapstructOrderServiceMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class MapstructOrderControllerMapper implements OrderControllerConverter {

    /**
     * Maps the service response to a controller response, performing currency conversion
     * for each product and calculating the order's total price.
     * <p>
     * The process includes:
     * <ul>
     *     <li>Converting individual product prices using the provided {@code exchangeRate}.</li>
     *     <li>Aggregating the sum of all products (price * quantity).</li>
     *     <li>Rounding the total price to 2 decimal places.</li>
     * </ul>
     *
     * @param serviceResponse the source order data from the service layer
     * @param exchangeRate    the context containing the target currency and its rate value
     * @return a {@link GetOrderResponse} with converted prices and calculated total
     */
    @Override
    public GetOrderResponse toResponse(
            BaseOrderServiceResponse serviceResponse,
            @Context ExchangeRate exchangeRate
    ) {
        var convertedProducts = serviceResponse.products().stream()
                .map(it -> mapOrderProductResponse(it, exchangeRate.rate()))
                .toList();

        var totalPrice = calculateTotal(convertedProducts);

        return GetOrderResponse.builder()
                .id(serviceResponse.orderId())
                .products(convertedProducts)
                .totalPrice(totalPrice.setScale(2, RoundingMode.HALF_UP))
                .currency(exchangeRate.currency())
                .build();
    }

    @Override
    @Mapping(
            target = "productQuantities", source = "createRequest.products",
            qualifiedByName = "requestProductsListToProductQuantities"
    )
    public abstract OrderServiceCreateCommand toServiceCommand(OrderCreateRequest createRequest);

    @Override
    @Mapping(source = "request", target = "productQuantities", qualifiedByName = "mapListToQuantitiesMap")
    public abstract OrderServiceUpdateCommand toServiceCommand(
            UUID orderId,
            Collection<OrderProductUpdateRequest> request
    );

    // ------------------------------------ HELPERS -------------------------------------------


    @Mapping(target = "price", source = "price", qualifiedByName = "exchangePrice")
    protected abstract GetOrderProductResponse mapOrderProductResponse(
            OrderServiceProductInOrderResponse src,
            @Context BigDecimal rateValue
    );

    /**
     * Converts list of product create requests to quantity map.
     * Aggregates quantities for duplicate product IDs.
     *
     * @param srcList list of product create requests
     * @return map of product ID to total quantity
     */
    @Named("requestProductsListToProductQuantities")
    protected Map<UUID, BigDecimal> requestProductsListToProductQuantities(
            List<OrderProductCreateRequest> srcList
    ) {
        return srcList.stream().collect(Collectors.toMap(
                OrderProductCreateRequest::id,
                OrderProductCreateRequest::quantity,
                BigDecimal::add
        ));
    }

    /**
     * Converts collection of product update requests to quantity map.
     * Aggregates quantities for duplicate product IDs.
     *
     * @param requestList collection of product update requests
     * @return map of product ID to total quantity
     */
    @Named("mapListToQuantitiesMap")
    protected Map<UUID, BigDecimal> mapListToQuantitiesMap(
            Collection<OrderProductUpdateRequest> requestList
    ) {
        return requestList.stream().collect(Collectors.toMap(
                OrderProductUpdateRequest::id,
                OrderProductUpdateRequest::quantity,
                BigDecimal::add
        ));
    }

    /**
     * Converts the source price to a target currency using the provided exchange rate.
     *
     * @param sourcePrice the original price in base currency
     * @param rateValue   the exchange rate provided via @Context
     * @return converted price
     */
    @Named("exchangePrice")
    protected BigDecimal exchangePrice(BigDecimal sourcePrice, @Context BigDecimal rateValue) {
        return PriceConverter.convert(
                PriceConverterRequest.builder()
                        .sourcePrice(sourcePrice)
                        .actualRate(rateValue)
                        .build()
        );
    }

    private BigDecimal calculateTotal(List<GetOrderProductResponse> products) {
        return products.stream()
                .map(p -> p.price().multiply(p.quantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
