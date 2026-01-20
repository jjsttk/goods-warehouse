package com.jjsttk.goodswarehouse.mapper.product.mapstruct;


import com.jjsttk.goodswarehouse.mapper.product.ProductReservationConverter;
import com.jjsttk.goodswarehouse.service.product.dto.command.ReserveProductCommandInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
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
public abstract class MapstructProductReservationMapper implements ProductReservationConverter {

    @Override
    @Mapping(target = "productQuantities", source = "productQuantities")
    public abstract ReserveProductCommandInfo toReserveCommand(Map<UUID, BigDecimal> productQuantities);
}
