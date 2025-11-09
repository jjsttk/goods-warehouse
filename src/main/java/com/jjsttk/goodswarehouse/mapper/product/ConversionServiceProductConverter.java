package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.response.BaseProductServiceDto;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class ConversionServiceProductConverter implements ProductConverter {

    private final ConversionService conversionService;


    @Override
    public ProductEntity mapToEntity(ProductServiceCreateCommand createCommandDtO) {
        return conversionService.convert(createCommandDtO, ProductEntity.class);
    }

    @Override
    public ProductServiceCreateCommand mapToServiceCommand(CreateProductRequest requestCreateDto) {
        return conversionService.convert(requestCreateDto, ProductServiceCreateCommand.class);
    }

    @Override
    public ProductServiceUpdateCommand mapToServiceCommand(UpdateProductRequest requestUpdateDto) {
        return conversionService.convert(requestUpdateDto, ProductServiceUpdateCommand.class);
    }

    @Override
    public BaseProductServiceDto mapToServiceResponse(ProductEntity entity) {
        return conversionService.convert(entity, BaseProductServiceDto.class);
    }

    @Override
    public GetProductResponse mapToControllerResponse(
            ProductPriceExchangeServiceResponse priceProductPriceExchangeServiceResponse
    ) {
        return conversionService.convert(priceProductPriceExchangeServiceResponse, GetProductResponse.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PageGetProductResponse<GetProductResponse> mapToControllerResponse(
            Page<ProductPriceExchangeServiceResponse> priceProductPriceExchangeServiceResponse
    ) {
        return conversionService.convert(priceProductPriceExchangeServiceResponse, PageGetProductResponse.class);
    }
}





