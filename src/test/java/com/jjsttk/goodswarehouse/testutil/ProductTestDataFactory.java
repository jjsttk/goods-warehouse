package com.jjsttk.goodswarehouse.testutil;

import com.jjsttk.goodswarehouse.dto.request.CreateProductRequestDto;
import com.jjsttk.goodswarehouse.dto.request.UpdateProductRequestDto;
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

    public static CreateProductRequestDto getCreateProductRequestDto(Product product) {
        return Instancio.of(CreateProductRequestDto.class)
                .set(field("name"), product.getName())
                .set(field("article"), product.getArticle())
                .set(field("category"), product.getCategory().name())
                .set(field("quantity"), product.getQuantity())
                .set(field("price"), product.getPrice())
                .set(field("description"), product.getDescription())
                .create();
    }

    public static UpdateProductRequestDto getUpdateProductRequestDto(Product product) {
        return Instancio.of(UpdateProductRequestDto.class)
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
