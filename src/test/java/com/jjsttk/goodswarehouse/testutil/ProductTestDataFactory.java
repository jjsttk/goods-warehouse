package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.dto.request.ProductRequestCreateDto;
import com.jjsttk.goodswarehouse.dto.request.ProductRequestUpdateDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.model.entity.Product;
import org.instancio.Instancio;

import static org.instancio.Select.field;


public class ProductTestDataFactory {

    public static Product getProductEntityWithoutGeneratedId() {
        return Instancio.of(Product.class)
                .ignore(field("id"))
                .create();
    }

    public static Product getProductEntityWithGeneratedId() {
        return Instancio.of(Product.class).create();
    }

    public static ProductRequestCreateDto getCreateProductRequestDto(Product product) {
        return Instancio.of(ProductRequestCreateDto.class)
                .set(field("name"), product.getName())
                .set(field("article"), product.getArticle())
                .set(field("category"), product.getCategory().name())
                .set(field("quantity"), product.getQuantity())
                .set(field("price"), product.getPrice())
                .set(field("description"), product.getDescription())
                .create();
    }

    public static ProductRequestUpdateDto getUpdateProductRequestDto(Product product) {
        return Instancio.of(ProductRequestUpdateDto.class)
                .set(field("name"), product.getName())
                .set(field("article"), product.getArticle())
                .set(field("category"), product.getCategory().name())
                .set(field("quantity"), product.getQuantity())
                .set(field("price"), product.getPrice())
                .set(field("description"), product.getDescription())
                .create();
    }

    public static ProductResponseDto getProductResponseDto(Product product) {
        return Instancio.of(ProductResponseDto.class)
                .set(field("id"), product.getId())
                .set(field("name"), product.getName())
                .set(field("article"), product.getArticle())
                .set(field("category"), product.getCategory().name())
                .set(field("quantity"), product.getQuantity())
                .set(field("price"), product.getPrice())
                .set(field("description"), product.getDescription())
                .set(field("createdAt"), product.getCreatedAt())
                .set(field("lastQuantityModified"), product.getLastQuantityModified())
                .create();
    }
}
