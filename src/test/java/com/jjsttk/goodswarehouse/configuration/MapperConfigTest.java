package com.jjsttk.goodswarehouse.configuration;

import com.jjsttk.goodswarehouse.configuration.properties.MapperProperties;
import com.jjsttk.goodswarehouse.mapper.ConversionServiceProductConverter;
import com.jjsttk.goodswarehouse.mapper.MapstructProductMapper;
import com.jjsttk.goodswarehouse.mapper.ProductConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapperConfigTest {

    @InjectMocks
    private MapperConfig sut;

    @Mock
    private MapstructProductMapper mapstructMapperMock;

    @Mock
    private ConversionServiceProductConverter conversionServiceMapperMock;

    @Mock
    private MapperProperties mapperPropertiesMock;

    private final String mapstructStub = "mapstruct";
    private final String conversionServiceStub = "conversion-service";

    @Test
    void shouldReturnMapstructMapperWhenTypeIsMapstruct() {
        when(mapperPropertiesMock.getType()).thenReturn(mapstructStub);

        var result = sut.productConverterSelector(
                mapstructMapperMock, conversionServiceMapperMock, mapperPropertiesMock);

        assertThat(result).isEqualTo(mapstructMapperMock);
    }

    @Test
    void shouldReturnConversionServiceMapperWhenTypeIsConversionService() {
        when(mapperPropertiesMock.getType()).thenReturn(conversionServiceStub);

        ProductConverter result = sut.productConverterSelector(
                mapstructMapperMock, conversionServiceMapperMock, mapperPropertiesMock);

        assertThat(result).isEqualTo(conversionServiceMapperMock);
    }

    @Test
    void shouldThrowExceptionWhenTypeIsUnknown() {
        when(mapperPropertiesMock.getType()).thenReturn("unknown");

        assertThrows(IllegalArgumentException.class, () ->
                sut.productConverterSelector(
                        mapstructMapperMock, conversionServiceMapperMock, mapperPropertiesMock));
    }
}
