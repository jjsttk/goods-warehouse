package com.jjsttk.goodswarehouse.unit.mapper;

import com.jjsttk.goodswarehouse.dto.request.CreateProductRequestDto;
import com.jjsttk.goodswarehouse.dto.response.ProductResponseDto;
import com.jjsttk.goodswarehouse.mapper.ConversionServiceProductConverter;
import com.jjsttk.goodswarehouse.model.entity.Product;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ConversionServiceProductConverterTest {

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private ConversionServiceProductConverter converter;

    private Product product;
    private CreateProductRequestDto createDto;
    private ProductResponseDto responseDto;

    @BeforeEach
    void setUp() {
        product = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        createDto = ProductTestDataFactory.getCreateProductRequestDto(product);
        responseDto = ProductTestDataFactory.getProductResponseDto(product);
    }

    @Test
    void mapToEntity_shouldDelegateToConversionService() {
        var expectedProduct = product;
        when(conversionService.convert(createDto, Product.class)).thenReturn(expectedProduct);
        var actual = converter.mapToEntity(createDto);

        assertThat(actual).isSameAs(expectedProduct);
        verify(conversionService, times(1)).convert(createDto, Product.class);
    }

    @Test
    void mapToDto_shouldDelegateToConversionService() {
        var expectedDto = responseDto;

        when(conversionService.convert(product, ProductResponseDto.class)).thenReturn(expectedDto);

        var actual = converter.mapToDto(product);

        assertThat(actual).isSameAs(expectedDto);
        verify(conversionService, times(1)).convert(product, ProductResponseDto.class);
    }
}