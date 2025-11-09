package com.jjsttk.goodswarehouse.mapper.product.converter;

import com.jjsttk.goodswarehouse.mapper.product.converter.CreateProductRequestToProductServiceCreateCommandConverter;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestCreateDtoToProductServiceCreateCommandConverterTest {
    private CreateProductRequestToProductServiceCreateCommandConverter converter;

    @BeforeEach
    void setUp() {
        converter = new CreateProductRequestToProductServiceCreateCommandConverter();
    }

    @Test
    void convertShouldMapAllFieldsCorrectlyWhenAllFieldsArePresent() {
        var entity = ProductTestDataFactory.getProductEntityWithGeneratedId();
        var src = ProductTestDataFactory.getCreateProductRequest(entity);

        var result = converter.convert(src);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(src.getName());
        assertThat(result.getArticle()).isEqualTo(src.getArticle());
        assertThat(result.getCategory()).isEqualTo(src.getCategory());
        assertThat(result.getQuantity()).isEqualTo(src.getQuantity());
        assertThat(result.getPrice()).isEqualTo(src.getPrice());
        assertThat(result.getDescription()).isEqualTo(src.getDescription());
    }
}
