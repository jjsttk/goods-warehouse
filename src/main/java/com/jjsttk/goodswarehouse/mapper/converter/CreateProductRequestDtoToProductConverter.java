package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.dto.request.CreateProductRequestDto;
import com.jjsttk.goodswarehouse.model.entity.Product;
import com.jjsttk.goodswarehouse.model.enums.Category;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public final class CreateProductRequestDtoToProductConverter implements Converter<CreateProductRequestDto, Product> {

    @Override
    public Product convert(CreateProductRequestDto source) {
        return Product.builder()
                .name(source.getName())
                .article(source.getArticle())
                .category(Category.valueOf(source.getCategory()))
                .quantity(source.getQuantity())
                .price(source.getPrice())
                .description(source.getDescription())
                .build();
    }
}
