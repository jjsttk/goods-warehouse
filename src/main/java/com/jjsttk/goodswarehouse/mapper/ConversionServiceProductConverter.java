package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class ConversionServiceProductConverter implements ProductConverter {

    private final ConversionService conversionService;


    @Override
    public ProductEntity mapToEntity(ProductCreateCommand createCommandDtO) {
        return conversionService.convert(createCommandDtO, ProductEntity.class);
    }

    @Override
    public GetProductResponse mapToControllerResponse(ProductServiceResponse productServiceResponse) {
        return conversionService.convert(productServiceResponse, GetProductResponse.class);
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
    @SuppressWarnings("unchecked")
    public GetPageProductResponse<GetProductResponse> mapToControllerResponse(
            Page<ProductServiceResponse> serviceResponse
    ) {
        return conversionService.convert(serviceResponse, GetPageProductResponse.class);
    }
}





