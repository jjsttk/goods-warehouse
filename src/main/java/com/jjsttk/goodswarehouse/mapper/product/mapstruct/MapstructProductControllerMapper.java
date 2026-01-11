package com.jjsttk.goodswarehouse.mapper.product.mapstruct;


import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.mapper.product.ProductControllerConverter;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeRate;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import com.jjsttk.goodswarehouse.shared.util.price.PriceConverter;
import com.jjsttk.goodswarehouse.shared.util.price.dto.request.PriceConverterRequest;
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
        imports = {Collections.class, PriceConverterRequest.class, PriceConverter.class},
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
    public abstract ProductServiceCreateCommand toCommand(CreateProductRequest createRequest);

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
    public abstract ProductServiceUpdateCommand toCommand(UpdateProductRequest updateRequest);

    @Override
    @Mappings({
            @Mapping(source = "serviceResponse.totalElements", target = "totalCount"),
            @Mapping(source = "serviceResponse.number", target = "currentPage"),
            @Mapping(source = "serviceResponse.size", target = "pageSize"),
            @Mapping(source = "serviceResponse.numberOfElements", target = "currentPageSize"),
            @Mapping(target = "content",
                    expression = "java(serviceResponse.hasContent() ? "
                                 + "mapContent(serviceResponse.getContent(), exchangeRate)"
                                 + " : Collections.emptyList())")
    })
    public abstract PageGetProductResponse<GetProductResponse> toResponse(
            Page<ProductServiceProductDetailedResponse> serviceResponse,
            ExchangeRate exchangeRate
    );

    @Mappings({
            @Mapping(target = "id", source = "source.id"),
            @Mapping(target = "name", source = "source.name"),
            @Mapping(target = "article", source = "source.article"),
            @Mapping(target = "description", source = "source.description"),
            @Mapping(target = "category", source = "source.category"),
            @Mapping(target = "price", expression = "java(PriceConverter.convert("
                                                    + "PriceConverterRequest.builder()"
                                                    + ".sourcePrice(source.price())"
                                                    + ".actualRate(exchangeRate.rate())"
                                                    + ".build()"
                                                    + "))"
            ),
            @Mapping(target = "quantity", source = "source.quantity"),
            @Mapping(target = "isAvailable", source = "source.isAvailable"),
            @Mapping(target = "currency", source = "exchangeRate.currency"),
            @Mapping(target = "lastQuantityModified", source = "source.lastQuantityModified"),
            @Mapping(target = "createdAt", source = "source.createdAt")
    })
    public abstract GetProductResponse toResponse(
            ProductServiceProductDetailedResponse source,
            ExchangeRate exchangeRate
    );

    // ------------------------------------ HELPERS -------------------------------------------

    /**
     * Maps a list of service responses to a list of API responses with currency conversion.
     *
     * <p>This helper method converts each {@link ProductServiceProductDetailedResponse} to a
     * {@link GetProductResponse} using the provided exchange rate for price conversion.</p>
     *
     * @param serviceResponse list of detailed product responses from the service layer
     * @param exchangeRate    exchange rate information for price conversion
     * @return list of API response objects, empty if input is null or empty
     */
    protected List<GetProductResponse> mapContent(
            List<ProductServiceProductDetailedResponse> serviceResponse,
            ExchangeRate exchangeRate
    ) {
        return serviceResponse.stream().map(it -> toResponse(it, exchangeRate)).toList();
    }

}
