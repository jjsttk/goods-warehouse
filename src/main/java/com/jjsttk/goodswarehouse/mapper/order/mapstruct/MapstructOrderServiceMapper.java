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
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        uses = MapstructReferenceMapper.class,
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
    })
    public abstract BaseOrderResponse toResponse(
            UUID orderId,
            OrderProductResponseContainer<OrderProductProjection> serviceResponse
    );
}
