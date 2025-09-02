package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.time.OffsetDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ConversionServiceProductConverterTest {

    @Mock
    private ConversionService conversionServiceMock;

    @InjectMocks
    private ConversionServiceProductConverter sut;

    private ProductEntity productStub;
    private ProductCreateCommand createCommandStub;
    private ProductServiceResponse serviceResponseStub;
    private GetProductResponse controllerResponseStub;

    @BeforeEach
    void setUp() {
        productStub = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        createCommandStub = ProductTestDataFactory.getProductCreateCommand(productStub);
        serviceResponseStub = ProductTestDataFactory.getProductServiceResponse(productStub);
        controllerResponseStub = ProductTestDataFactory.getGetProductResponse(productStub);
    }

    @Test
    void mapperShouldHandleDifferentTimeZonesCorrectly() {
        OffsetDateTime utcTime = OffsetDateTime.parse("2023-01-01T12:00:00Z");
        OffsetDateTime tokyoTime = OffsetDateTime.parse("2023-01-01T12:00:00+09:00");
        OffsetDateTime newYorkTime = OffsetDateTime.parse("2023-01-01T12:00:00-05:00");

        when(conversionServiceMock.convert(productStub, ProductServiceResponse.class))
                .thenAnswer(invocation -> {
                    ProductEntity entity = invocation.getArgument(0);
                    return ProductServiceResponse.builder()
                            .lastQuantityModified(entity.getLastQuantityModified())
                            .build();
                });

        productStub.setLastQuantityModified(utcTime);
        System.out.println("utc : " + productStub.getLastQuantityModified());
        ProductServiceResponse response = sut.mapToServiceResponse(productStub);
        assertThat(response.lastQuantityModified()).isEqualTo(utcTime);

        productStub.setLastQuantityModified(tokyoTime);
        System.out.println("tokyoTime : " + productStub.getLastQuantityModified());
        response = sut.mapToServiceResponse(productStub);
        assertThat(response.lastQuantityModified()).isEqualTo(tokyoTime);

        productStub.setLastQuantityModified(newYorkTime);
        response = sut.mapToServiceResponse(productStub);
        assertThat(response.lastQuantityModified()).isEqualTo(newYorkTime);
    }

    @Test
    void mapToEntityShouldConvertCreateCommandToEntity() {
        when(conversionServiceMock.convert(createCommandStub, ProductEntity.class))
                .thenReturn(productStub);

        ProductEntity result = sut.mapToEntity(createCommandStub);

        assertThat(result).isEqualTo(productStub);
    }

    @Test
    void mapToControllerResponseShouldConvertServiceResponseToControllerResponse() {
        when(conversionServiceMock.convert(serviceResponseStub, GetProductResponse.class))
                .thenReturn(controllerResponseStub);

        GetProductResponse result = sut.mapToControllerResponse(serviceResponseStub);

        assertThat(result).isEqualTo(controllerResponseStub);
    }
}
