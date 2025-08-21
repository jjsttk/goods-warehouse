package com.jjsttk.goodswarehouse.unit.mapper.converter;

import com.jjsttk.goodswarehouse.dto.request.CreateProductRequestDto;
import com.jjsttk.goodswarehouse.mapper.converter.CreateProductRequestDtoToProductConverter;
import com.jjsttk.goodswarehouse.model.entity.Product;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateProductRequestDtoToProductConverterTest {

    private CreateProductRequestDtoToProductConverter converter;
    private Product product;
    private CreateProductRequestDto createDto;

    @BeforeEach
    void setUp() {
        converter = new CreateProductRequestDtoToProductConverter();
        product = ProductTestDataFactory.getProductEntityWithGeneratedId();
        createDto = ProductTestDataFactory.getCreateProductRequestDto(product);

    }

    @Test
    void convert_shouldMapAllFieldsCorrectly() {
        var expectedProduct = product;
        var actualProduct = converter.convert(createDto);

        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void convert_shouldThrowException_whenInvalidCategory() {
        createDto.setCategory("exception category");

        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(createDto));
    }
}

