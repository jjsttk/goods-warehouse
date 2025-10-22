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
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductConverter {
    ProductEntity mapToEntity(ProductCreateCommand requestCreateDtO);

    ProductCreateCommand mapToServiceCommand(CreateProductRequest requestCreateDto);

    ProductUpdateCommand mapToServiceCommand(UpdateProductRequest requestUpdateDto);

    ProductServiceResponse mapToServiceResponse(ProductEntity entity);

    GetProductResponse mapToControllerResponse(
            ProductServiceResponse productServiceResponse,
            ExchangeServiceResponse priceExchangeServiceResponse);

    GetPageProductResponse<GetProductResponse> mapToControllerResponse(
            Page<ProductServiceResponse> productServiceResponse,
            List<ExchangeServiceResponse> priceExchangeServiceResponse);
}
