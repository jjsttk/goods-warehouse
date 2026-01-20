package com.jjsttk.goodswarehouse.mapper.product.spring;

import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.mapper.product.ProductControllerConverter;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.UpdateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ExchangeProductPriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public final class ConversionServiceProductControllerConverter implements ProductControllerConverter {
    private final ConversionService conversionService;

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
            Page<ExchangeProductPriceResponse> serviceResponse
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
    public GetProductResponse toResponse(ExchangeProductPriceResponse serviceResponse) {
        return Objects.requireNonNull(conversionService.convert(serviceResponse, GetProductResponse.class));
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
