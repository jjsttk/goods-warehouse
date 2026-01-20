package com.jjsttk.goodswarehouse.mapper.product.mapstruct;


import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.mapper.product.ProductControllerConverter;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.UpdateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ExchangeProductPriceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@Mapper(
        imports = Collections.class,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class MapstructProductControllerMapper implements ProductControllerConverter {

    @Override
    @Mappings({
            @Mapping(target = "name",
                    expression = "java(createRequest.name() == null"
                                 + " ? null : createRequest.name().strip())"),

            @Mapping(target = "article",
                    expression = "java(createRequest.article() == null"
                                 + " ? null : createRequest.article().strip())"),

            @Mapping(target = "description",
                    expression = "java(createRequest.description() == null"
                                 + " ? null : createRequest.description().strip())")
    })
    public abstract CreateProductCommandInfo toCommand(CreateProductRequest createRequest);

    @Override
    @Mappings({
            @Mapping(target = "name",
                    expression = "java(updateRequest.name() == null"
                                 + " ? null : updateRequest.name().strip())"),

            @Mapping(target = "article",
                    expression = "java(updateRequest.article() == null"
                                 + " ? null : updateRequest.article().strip())"),

            @Mapping(target = "description",
                    expression = "java(updateRequest.description() == null"
                                 + " ? null : updateRequest.description().strip())")
    })
    public abstract UpdateProductCommandInfo toCommand(UpdateProductRequest updateRequest);

    @Override
    @Mappings({
            @Mapping(source = "totalElements", target = "totalCount"),
            @Mapping(source = "number", target = "currentPage"),
            @Mapping(source = "size", target = "pageSize"),
            @Mapping(source = "numberOfElements", target = "currentPageSize"),
            @Mapping(target = "content",
                    expression = "java(serviceResponse.hasContent() ? "
                                 + "mapContent(serviceResponse.getContent())"
                                 + " : Collections.emptyList())")
    })
    public abstract PageGetProductResponse<GetProductResponse> toResponse(
            Page<ExchangeProductPriceResponse> serviceResponse
    );

    protected abstract List<GetProductResponse> mapContent(List<ExchangeProductPriceResponse> sourceList);


}
