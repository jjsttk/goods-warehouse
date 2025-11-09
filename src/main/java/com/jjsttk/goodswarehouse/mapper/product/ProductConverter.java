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
