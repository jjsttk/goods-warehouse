package com.jjsttk.goodswarehouse.unit.mapper;

import com.jjsttk.goodswarehouse.mapper.ConversionServiceProductConverter;
import com.jjsttk.goodswarehouse.mapper.MapstructProductMapper;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import com.jjsttk.goodswarehouse.mapper.configuration.MapperProperties;
import com.jjsttk.goodswarehouse.mapper.configuration.MapperSelector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapperSelectorTest {

    @InjectMocks
    private MapperSelector mapperSelector;

    @Mock
    private MapstructProductMapper mapstructMapper;

    @Mock
    private ConversionServiceProductConverter conversionServiceMapper;

    @Mock
    private MapperProperties mapperProperties;


    @Test
    void shouldReturnMapstructMapper_whenTypeIsMapstruct() {
        when(mapperProperties.getType()).thenReturn("mapstruct");

        var result = mapperSelector.productConverterSelector(
                mapstructMapper, conversionServiceMapper, mapperProperties);

        assertThat(result).isEqualTo(mapstructMapper);
    }

    @Test
    void shouldReturnConversionServiceMapper_whenTypeIsConversionService() {
        when(mapperProperties.getType()).thenReturn("conversion-service");

        ProductConverter result = mapperSelector.productConverterSelector(
                mapstructMapper, conversionServiceMapper, mapperProperties);

        assertThat(result).isEqualTo(conversionServiceMapper);
    }

    @Test
    void shouldThrowException_whenTypeIsUnknown() {
        when(mapperProperties.getType()).thenReturn("unknown");

        assertThrows(IllegalArgumentException.class, () ->
                mapperSelector.productConverterSelector(
                        mapstructMapper, conversionServiceMapper, mapperProperties));
    }


}
