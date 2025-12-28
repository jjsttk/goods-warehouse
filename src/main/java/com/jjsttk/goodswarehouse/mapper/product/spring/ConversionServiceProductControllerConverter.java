package com.jjsttk.goodswarehouse.mapper.product.spring;

import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.mapper.product.ProductControllerConverter;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public final class ConversionServiceProductControllerConverter implements ProductControllerConverter {
    private final ConversionService conversionService;

    @Override
    public ProductServiceCreateCommand toCommand(CreateProductRequest createRequest) {
        return ProductServiceCreateCommand.builder()
                .name(stripOrNull(createRequest.name()))
                .article(stripOrNull(createRequest.article()))
                .description(stripOrNull(createRequest.description()))
                .category(createRequest.category())
                .price(createRequest.price())
                .quantity(createRequest.quantity())
                .isAvailable(createRequest.isAvailable())
                .build();
    }

    @Override
    public ProductServiceUpdateCommand toCommand(UpdateProductRequest updateRequest) {
        return ProductServiceUpdateCommand.builder()
                .name(stripOrNull(updateRequest.name()))
                .price(updateRequest.price())
                .description(stripOrNull(updateRequest.description()))
                .article(stripOrNull(updateRequest.article()))
                .category(updateRequest.category())
                .quantity(updateRequest.quantity())
                .isAvailable(updateRequest.isAvailable())
                .build();
    }

    @Override
    public PageGetProductResponse<GetProductResponse> toResponse(
            Page<ProductPriceExchangeServiceResponse> serviceResponse
    ) {
        var convertedContent = serviceResponse.getContent().stream()
                .map(it -> conversionService.convert(it, GetProductResponse.class))
                .toList();

        return PageGetProductResponse.<GetProductResponse>builder()
                .content(convertedContent)
                .totalCount(serviceResponse.getTotalElements())
                .totalPages(serviceResponse.getTotalPages())
                .currentPage(serviceResponse.getNumber())
                .pageSize(serviceResponse.getSize())
                .currentPageSize(serviceResponse.getNumberOfElements())
                .build();
    }

    @Override
    public GetProductResponse toResponse(ProductPriceExchangeServiceResponse serviceResponse) {
        return conversionService.convert(serviceResponse, GetProductResponse.class);
    }

    /**
     * Normalizes input string by trimming whitespace.
     * Returns null if input is null, otherwise returns trimmed string.
     *
     * @param str input string to normalize, may be null
     * @return trimmed string or null if input was null
     */
    private String stripOrNull(String str) {
        if (Objects.nonNull(str)) {
            return str.strip();
        }
        return null;
    }
}
