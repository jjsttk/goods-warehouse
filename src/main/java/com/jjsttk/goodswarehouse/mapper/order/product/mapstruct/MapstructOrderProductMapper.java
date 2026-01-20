package com.jjsttk.goodswarehouse.mapper.order.product.mapstruct;

import com.jjsttk.goodswarehouse.mapper.order.product.OrderProductConverter;
import com.jjsttk.goodswarehouse.mapper.util.mapstruct.MapstructReferenceMapper;
import com.jjsttk.goodswarehouse.persistence.entity.order.product.OrderProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.response.ReservedProductInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        uses = {
                MapstructReferenceMapper.class,
        },
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class MapstructOrderProductMapper implements OrderProductConverter {

    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "order", ignore = true),
            @Mapping(target = "product", source = "productId"),
            @Mapping(target = "orderedQuantity", source = "productInfo.reservedQuantity"),
            @Mapping(target = "price", source = "productInfo.priceAtMoment"),
    })
    public abstract OrderProductEntity toEntity(
            UUID productId,
            ReservedProductInfo productInfo
    );

    // ----------------------------------------------------------------------------------------------------------------


    /**
     * Updates the ordered quantity in an existing order product entity using delta values.
     * <p>
     * The quantity from {@code updateDto} is treated as a delta value and is added
     * to the current ordered quantity in the entity.
     * </p>
     *
     * <p><b>Example:</b> Current quantity = 5, delta = 3 → Result = 8</p>
     *
     * @param orderProductEntity entity to update, not null
     */
    @Override
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "order", ignore = true),
            @Mapping(target = "product", ignore = true),
    })
    public void update(
            @MappingTarget OrderProductEntity orderProductEntity,
            ReservedProductInfo updateInfo
    ) {
        var currentQuantity = orderProductEntity.getOrderedQuantity();
        var deltaQuantity = updateInfo.reservedQuantity();

        orderProductEntity.setOrderedQuantity(currentQuantity.add(deltaQuantity));
    }
}
