package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRequestCreateDtoToProductCreateCommandConverterTest {
    private ProductRequestCreateDtoToProductCreateCommandConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ProductRequestCreateDtoToProductCreateCommandConverter();
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
