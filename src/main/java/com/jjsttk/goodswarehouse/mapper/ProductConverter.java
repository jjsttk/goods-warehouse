package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.request.CreateProductRequest;
import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.controller.request.UpdateProductRequest;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;

public interface ProductConverter {
    ProductEntity mapToEntity(ProductCreateCommand requestCreateDtO);
    GetProductResponse mapToControllerResponse(ProductServiceResponse productServiceResponse);
    GetPageProductResponse<GetProductResponse> mapToControllerResponse(Page<ProductServiceResponse> serviceResponse);
    ProductCreateCommand mapToServiceCommand(CreateProductRequest requestCreateDto);
    ProductUpdateCommand mapToServiceCommand(UpdateProductRequest requestUpdateDto);
    ProductServiceResponse mapToServiceResponse(ProductEntity entity);
}
