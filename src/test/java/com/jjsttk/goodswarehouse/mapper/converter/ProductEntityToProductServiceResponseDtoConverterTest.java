package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductEntityToProductServiceResponseDtoConverterTest {
    private ProductEntityToProductServiceResponseDtoConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ProductEntityToProductServiceResponseDtoConverter();
    }

    @Test
    void convertShouldMapAllFieldsCorrectlyWhenAllFieldsArePresent() {
        var src = ProductTestDataFactory.getProductEntityWithGeneratedId();

        var result = converter.convert(src);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(src.getName());
        assertThat(result.article()).isEqualTo(src.getArticle());
        assertThat(result.category()).isEqualTo(src.getCategory());
        assertThat(result.quantity()).isEqualTo(src.getQuantity());
        assertThat(result.price()).isEqualTo(src.getPrice());
        assertThat(result.description()).isEqualTo(src.getDescription());
        assertThat(result.createdAt()).isEqualTo(src.getCreatedAt());
        assertThat(result.lastQuantityModified()).isEqualTo(src.getLastQuantityModified());
    }
}
