package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.controller.product.dto.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.service.product.dto.command.CreateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.dto.command.UpdateProductCommandInfo;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ExchangeProductPriceResponse;
import org.springframework.data.domain.Page;

public interface ProductControllerConverter {

    CreateProductCommandInfo toCommand(CreateProductRequest createRequest);

    UpdateProductCommandInfo toCommand(UpdateProductRequest updateRequest);

    PageGetProductResponse<GetProductResponse> toResponse(
            Page<ExchangeProductPriceResponse> serviceResponse
    );

    GetProductResponse toResponse(
            ExchangeProductPriceResponse serviceResponse
    );
}
