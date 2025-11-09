package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.BaseProductServiceDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting {@link BaseProductServiceDto} objects
 * to {@link GetProductResponse} and {@link PageGetProductResponse} DTOs
 * used in controller responses.
 * <p>
 * Supports mapping single products as well as paginated lists of products
 * with corresponding exchange rate adjustments.
 * <p>
 * Null values are ignored during mapping to prevent overwriting existing fields.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class MapstructProductMapper implements ProductConverter {
}
