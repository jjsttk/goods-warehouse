package com.jjsttk.goodswarehouse.mapper.product.spring;

import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.mapper.product.ProductControllerConverter;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeRate;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.UpdateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductDetailedResponse;
import com.jjsttk.goodswarehouse.shared.util.price.PriceConverter;
import com.jjsttk.goodswarehouse.shared.util.price.dto.request.PriceConverterRequest;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public final class ConversionServiceProductControllerConverter implements ProductControllerConverter {

    @Override
    public CreateProductCommandInfo toCommand(CreateProductRequest createRequest) {
        return CreateProductCommandInfo.builder()
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
    public UpdateProductCommandInfo toCommand(UpdateProductRequest updateRequest) {
        return UpdateProductCommandInfo.builder()
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
            Page<ProductDetailedResponse> serviceResponse,
            ExchangeRate exchangeRate
    ) {
        return PageGetProductResponse.<GetProductResponse>builder()
                .totalCount(serviceResponse.getTotalElements())
                .content(serviceResponse.getContent().stream()
                        .map(it -> toResponse(it, exchangeRate))
                        .toList())
                .totalPages(serviceResponse.getTotalPages())
                .currentPage(serviceResponse.getNumber())
                .pageSize(serviceResponse.getSize())
                .currentPageSize(serviceResponse.getNumberOfElements())
                .build();
    }

    @Override
    public GetProductResponse toResponse(
            ProductDetailedResponse serviceResponse,
            ExchangeRate exchangeRate
    ) {
        return GetProductResponse.builder()
                .id(serviceResponse.id())
                .name(serviceResponse.name())
                .article(serviceResponse.article())
                .description(serviceResponse.description())
                .category(serviceResponse.category())
                .price(PriceConverter.convert(
                        PriceConverterRequest.builder()
                                .sourcePrice(serviceResponse.price())
                                .actualRate(exchangeRate.rate())
                                .build()
                ))
                .currency(exchangeRate.currency())
                .quantity(serviceResponse.quantity())
                .lastQuantityModified(serviceResponse.lastQuantityModified())
                .description(serviceResponse.description())
                .isAvailable(serviceResponse.isAvailable())
                .createdAt(serviceResponse.createdAt())
                .build();
    }

    /**
     * Normalizes input string by trimming whitespace.
     * Returns null if input is null, otherwise returns trimmed string.
     *
     * @param str input string to normalize, may be null
     * @return trimmed string or null if input was null
     */
    private @Nullable String stripOrNull(@Nullable String str) {
        return str == null ? null : str.strip();
    }
}
