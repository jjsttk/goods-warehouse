package com.jjsttk.goodswarehouse.mapper.product.mapstruct;

import com.jjsttk.goodswarehouse.mapper.product.ProductServiceConverter;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceReservedProductInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class MapstructProductServiceMapper implements ProductServiceConverter {

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "lastQuantityModified", ignore = true)
    })
    public abstract ProductEntity toEntity(
            ProductServiceCreateCommand productServiceCreateCommand
    );

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "lastQuantityModified", ignore = true)
    })
    public abstract void update(
            @MappingTarget ProductEntity entity,
            ProductServiceUpdateCommand productServiceUpdateCommand
    );

    @Override
    @Mappings({
            @Mapping(target = "reservedQuantity", source = "reservedQuantity"),
            @Mapping(target = "priceAtMoment", source = "price")
    })
    public abstract ProductServiceReservedProductInfo toProductInfo(
            BigDecimal reservedQuantity,
            BigDecimal price
    );

    @Override
    @Mapping(target = "productInfo", source = "reservedProductsResponseMap")
    public abstract ProductServiceReservationResponse toResponse(
            Map<UUID, ProductServiceReservedProductInfo> reservedProductsResponseMap
    );
}
