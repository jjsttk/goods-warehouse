package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.dto.request.ProductRequestCreateDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.model.entity.Product;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public final class ConversionServiceProductConverter implements ProductConverter {

    private final ConversionService conversionService;


    @Override
    public Product mapToEntity(ProductRequestCreateDto createDtO) {
        return conversionService.convert(createDtO, Product.class);
    }

    @Override
    public ProductResponseDto mapToDto(Product entity) {
        return conversionService.convert(entity, ProductResponseDto.class);
    }
}
