package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.BaseProductServiceDto;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

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

    @Override
    @Mapping(source = "totalElements", target = "totalCount")
    @Mapping(source = "number", target = "currentPage")
    @Mapping(source = "size", target = "pageSize")
    @Mapping(source = "numberOfElements", target = "currentPageSize")
    public abstract PageGetProductResponse<GetProductResponse> mapToControllerResponse(
            Page<ProductPriceExchangeServiceResponse> serviceResponse
    );
}
