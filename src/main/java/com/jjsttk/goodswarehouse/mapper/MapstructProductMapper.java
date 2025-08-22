package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.dto.request.ProductRequestCreateDto;
import com.jjsttk.goodswarehouse.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public abstract class MapstructProductMapper implements ProductConverter {
    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lastQuantityModified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    public abstract Product mapToEntity(ProductRequestCreateDto createDto);
}
