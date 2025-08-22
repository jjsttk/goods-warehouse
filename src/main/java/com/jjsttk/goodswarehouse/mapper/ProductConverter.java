package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.dto.request.ProductRequestCreateDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.model.entity.Product;

public interface ProductConverter {
    Product mapToEntity(ProductRequestCreateDto createDtO);
    ProductResponseDto mapToDto(Product entity);
}
