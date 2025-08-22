package com.jjsttk.goodswarehouse.unit.mapper.converter;

import com.jjsttk.goodswarehouse.mapper.converter.ProductToProductResponseDtoConverter;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductToProductResponseDtoConverterTest {

    private final ProductToProductResponseDtoConverter converter =
            new ProductToProductResponseDtoConverter();

    @Test
    void convertShouldMapAllFieldsCorrectly() {
        var product = ProductTestDataFactory.getProductEntityWithGeneratedId();
        var expectedResponseDto = ProductTestDataFactory.getProductResponseDto(product);
        var actualResponseDto = converter.convert(product);

        assertEquals(expectedResponseDto, actualResponseDto);
    }
}
