package com.jjsttk.goodswarehouse.unit.mapper.converter;

import com.jjsttk.goodswarehouse.dto.request.ProductRequestCreateDto;
import com.jjsttk.goodswarehouse.mapper.converter.ProductRequestCreateDtoToProductConverter;
import com.jjsttk.goodswarehouse.model.entity.Product;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductRequestCreateDtoToProductConverterTest {

    private ProductRequestCreateDtoToProductConverter converter;
    private Product product;
    private ProductRequestCreateDto createDto;

    @BeforeEach
    void setUp() {
        converter = new ProductRequestCreateDtoToProductConverter();
        product = ProductTestDataFactory.getProductEntityWithGeneratedId();
        createDto = ProductTestDataFactory.getCreateProductRequestDto(product);

    }

    @Test
    void convertShouldMapAllFieldsCorrectly() {
        var expectedProduct = product;
        var actualProduct = converter.convert(createDto);

        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void convertShouldThrowExceptionWhenInvalidCategory() {
        createDto.setCategory("exception category");

        assertThrows(IllegalArgumentException.class,
                () -> converter.convert(createDto));
    }
}

