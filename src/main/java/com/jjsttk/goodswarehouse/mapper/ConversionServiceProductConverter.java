package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.service.exchange.response.ExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.product.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.response.ProductServiceResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public final class ConversionServiceProductConverter implements ProductConverter {

    private final ConversionService conversionService;


    @Override
    public ProductEntity mapToEntity(ProductCreateCommand createCommandDtO) {
        return conversionService.convert(createCommandDtO, ProductEntity.class);
    }

    @Override
    public ProductCreateCommand mapToServiceCommand(CreateProductRequest requestCreateDto) {
        return conversionService.convert(requestCreateDto, ProductCreateCommand.class);
    }

    @Override
    public ProductUpdateCommand mapToServiceCommand(UpdateProductRequest requestUpdateDto) {
        return conversionService.convert(requestUpdateDto, ProductUpdateCommand.class);
    }

    @Override
    public ProductServiceResponse mapToServiceResponse(ProductEntity entity) {
        return conversionService.convert(entity, ProductServiceResponse.class);
    }

    @Override
    public GetProductResponse mapToControllerResponse(
            ProductServiceResponse productServiceResponse,
            ExchangeServiceResponse exchangeServiceResponse
    ) {
        return GetProductResponse.builder()
                .id(productServiceResponse.id())
                .name(productServiceResponse.name())
                .article(productServiceResponse.article())
                .description(productServiceResponse.description())
                .category(productServiceResponse.category())
                .quantity(productServiceResponse.quantity())
                .price(exchangeServiceResponse.price())
                .currency(exchangeServiceResponse.currency())
                .lastQuantityModified(productServiceResponse.lastQuantityModified())
                .createdAt(productServiceResponse.createdAt())
                .build();
    }

    @Override
    public GetPageProductResponse<GetProductResponse> mapToControllerResponse(
            Page<ProductServiceResponse> productServiceResponse,
            List<ExchangeServiceResponse> priceExchangeServiceResponse
    ) {
        List<GetProductResponse> content = productServiceResponse
                .getContent()
                .stream()
                .map(req -> {
                    int index = productServiceResponse.getContent().indexOf(req);
                    ExchangeServiceResponse exchange = priceExchangeServiceResponse.get(index);
                    return mapToControllerResponse(req, exchange);
                })
                .toList();

        return GetPageProductResponse.<GetProductResponse>builder()
                .content(content)
                .totalCount(productServiceResponse.getTotalElements())
                .totalPages(productServiceResponse.getTotalPages())
                .currentPage(productServiceResponse.getNumber())
                .pageSize(productServiceResponse.getSize())
                .currentPageSize(productServiceResponse.getNumberOfElements())
                .build();
    }
}





