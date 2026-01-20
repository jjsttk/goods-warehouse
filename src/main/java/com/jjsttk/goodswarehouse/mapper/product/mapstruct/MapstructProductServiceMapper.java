package com.jjsttk.goodswarehouse.mapper.product.mapstruct;

import com.jjsttk.goodswarehouse.mapper.product.ProductServiceConverter;
import com.jjsttk.goodswarehouse.persistence.entity.product.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.UpdateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductReservationResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ReservedProductInfo;
import com.jjsttk.goodswarehouse.shared.enums.product.ReservationStatus;
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
            CreateProductCommandInfo createProductCommandInfo
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
            UpdateProductCommandInfo updateProductCommandInfo
    );

    @Override
    @Mappings({
            @Mapping(target = "reservedQuantity", source = "reservedQuantity"),
            @Mapping(target = "priceAtMoment", source = "price")
    })
    public abstract ReservedProductInfo toProductInfo(
            BigDecimal reservedQuantity,
            BigDecimal price
    );

    @Override
    @Mappings({
            @Mapping(target = "reservedProductsInfoMap", source = "reservedProductsResponseMap"),
            @Mapping(target = "problemsMap", source = "reservationProblemMap")
    })
    public abstract ProductReservationResponse toResponse(
            Map<UUID, ReservedProductInfo> reservedProductsResponseMap,
            Map<UUID, ReservationStatus> reservationProblemMap
    );
}
