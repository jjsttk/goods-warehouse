package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.enums.Category;
import com.jjsttk.goodswarehouse.service.product.command.ProductServiceCreateCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceCreateCommandToProductEntityConverterTest {
    private ProductServiceCreateCommandToProductEntityConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ProductServiceCreateCommandToProductEntityConverter();
    }

    @Test
    void convertShouldMapAllFieldsCorrectlyWhenAllFieldsArePresent() {
        var command = ProductServiceCreateCommand.builder()
                .name("Test Product")
                .article("TEST-001")
                .category(Category.ELECTRONICS)
                .quantity(BigDecimal.TEN)
                .price(new BigDecimal("99.99"))
                .description("Test description")
                .build();

        var result = converter.convert(command);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Product");
        assertThat(result.getArticle()).isEqualTo("TEST-001");
        assertThat(result.getCategory()).isEqualByComparingTo(Category.ELECTRONICS);
        assertThat(result.getQuantity()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
        assertThat(result.getDescription()).isEqualTo("Test description");
    }

}
