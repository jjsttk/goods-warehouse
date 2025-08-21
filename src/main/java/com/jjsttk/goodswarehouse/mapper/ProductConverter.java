package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.dto.request.CreateProductRequestDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.model.entity.Product;

public interface ProductConverter {
    Product mapToEntity(CreateProductRequestDto createDtO);
    ProductResponseDto mapToDto(Product entity);
}
