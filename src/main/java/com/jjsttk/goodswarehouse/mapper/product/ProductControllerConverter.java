package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import org.springframework.data.domain.Page;

public interface ProductControllerConverter {

    ProductServiceCreateCommand toCommand(CreateProductRequest createRequest);

    ProductServiceUpdateCommand toCommand(UpdateProductRequest updateRequest);

    PageGetProductResponse<GetProductResponse> toResponse(
            Page<ProductPriceExchangeServiceResponse> serviceResponse
    );

    GetProductResponse toResponse(
            ProductPriceExchangeServiceResponse serviceResponse
    );
}
