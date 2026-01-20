package com.jjsttk.goodswarehouse.mapper.order.mapstruct;

import com.jjsttk.goodswarehouse.mapper.order.OrderServiceConverter;
import com.jjsttk.goodswarehouse.mapper.util.mapstruct.MapstructReferenceMapper;
import com.jjsttk.goodswarehouse.persistence.entity.order.OrderEntity;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderResponse;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductProjection;
import com.jjsttk.goodswarehouse.service.order.product.dto.response.OrderProductResponseContainer;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.UUID;

@Mapper(
        uses = {
                MapstructReferenceMapper.class
        },
        imports = OrderStatus.class,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class MapstructOrderServiceMapper implements OrderServiceConverter {

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "customer", source = "customerId"),
            @Mapping(target = "orderProducts", ignore = true)
    })
    public abstract OrderEntity toEntity(
            Long customerId,
            String deliveryAddress
    );

    // ----------------------------------------------------------------------------------------------------------------

    @Override
    @Mappings({
            @Mapping(target = "products", source = "serviceResponse.orderProducts"),
            @Mapping(target = "totalPrice", source = "serviceResponse.orderProducts",
                    qualifiedByName = "calculateTotalPrice")
    })
    public abstract BaseOrderResponse toResponse(
            UUID orderId,
            OrderProductResponseContainer<OrderProductProjection> serviceResponse
    );

    /**
     * Calculates total price from list of product summaries.
     * Sums price × quantity for each product and rounds to 2 decimal places.
     *
     * @param products list of product summaries
     * @return total price rounded to 2 decimal places
     */
    @Named("calculateTotalPrice")
    protected BigDecimal calculateTotalPrice(Collection<OrderProductProjection> products) {
        return products.stream()
                .map(it -> it.price().multiply(it.quantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
