package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.price.exchange.response.ProductPriceExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.response.BaseProductServiceDto;
import org.springframework.data.domain.Page;

public interface ProductConverter {
    ProductEntity mapToEntity(ProductServiceCreateCommand requestCreateDtO);

    ProductServiceCreateCommand mapToServiceCommand(CreateProductRequest requestCreateDto);

    ProductServiceUpdateCommand mapToServiceCommand(UpdateProductRequest requestUpdateDto);

    BaseProductServiceDto mapToServiceResponse(ProductEntity entity);

    GetProductResponse mapToControllerResponse(
            ProductPriceExchangeServiceResponse priceProductPriceExchangeServiceResponse
    );

    PageGetProductResponse<GetProductResponse> mapToControllerResponse(
            Page<ProductPriceExchangeServiceResponse> priceProductPriceExchangeServiceResponse
    );
}
