package com.jjsttk.goodswarehouse.mapper.order.mapstruct;

import com.jjsttk.goodswarehouse.controller.order.dto.request.create.OrderCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.create.product.OrderProductCreateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.request.update.product.OrderProductUpdateRequest;
import com.jjsttk.goodswarehouse.controller.order.dto.response.GetOrderResponse;
import com.jjsttk.goodswarehouse.mapper.order.OrderControllerConverter;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.order.dto.command.OrderServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.order.price.dto.response.OrderPriceExchangeServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class MapstructOrderControllerMapper implements OrderControllerConverter {

    @Override
    @Mapping(target = "id", source = "orderId")
    public abstract GetOrderResponse toResponse(OrderPriceExchangeServiceResponse serviceResponse);

    @Override
    @Mapping(
            target = "productQuantities", source = "createRequest.products",
            qualifiedByName = "requestProductsListToProductQuantities"
    )
    public abstract OrderServiceCreateCommand toServiceCommand(OrderCreateRequest createRequest);

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


    @Override
    @Mapping(source = "request", target = "productQuantities", qualifiedByName = "mapListToQuantitiesMap")
    public abstract OrderServiceUpdateCommand toServiceCommand(
            UUID orderId,
            Collection<OrderProductUpdateRequest> request
    );

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


}
