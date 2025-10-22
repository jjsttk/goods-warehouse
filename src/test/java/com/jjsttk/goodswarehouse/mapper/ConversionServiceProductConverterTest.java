package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.service.exchange.response.ExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.command.ProductCreateCommand;
import com.jjsttk.goodswarehouse.service.product.command.ProductUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ConversionServiceProductConverterTest {
    @Mock
    private ConversionService conversionServiceMock;

    @InjectMocks
    private ConversionServiceProductConverter sut;

    private ProductEntity productStub;
    private ExchangeServiceResponse exchangeServiceResponseStub;
    private ProductCreateCommand createCommandStub;
    private ProductServiceResponse serviceResponseStub;

    @BeforeEach
    void setUp() {
        productStub = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        createCommandStub = ProductTestDataFactory.getProductCreateCommand(productStub);
        serviceResponseStub = ProductTestDataFactory.getProductServiceResponse(productStub);

        exchangeServiceResponseStub = ExchangeServiceResponse.builder()
                .price(BigDecimal.valueOf(123))
                .currency(PriceCurrency.RUB)
                .build();
    }

    @Test
    void mapToEntityShouldDelegateToConversionService() {
        when(conversionServiceMock.convert(createCommandStub, ProductEntity.class)).thenReturn(productStub);

        ProductEntity result = sut.mapToEntity(createCommandStub);

        assertThat(result).isSameAs(productStub);
    }

    @Test
    void mapToServiceCommandFromCreateRequestShouldDelegateToConversionService() {
        var request = ProductTestDataFactory.getCreateProductRequest(productStub);
        when(conversionServiceMock.convert(request, ProductCreateCommand.class)).thenReturn(createCommandStub);

        ProductCreateCommand result = sut.mapToServiceCommand(request);

        assertThat(result).isSameAs(createCommandStub);
    }

    @Test
    void mapToServiceCommandFromUpdateRequestShouldDelegateToConversionService() {
        var updateRequest = ProductTestDataFactory.getUpdateProductRequest(productStub);
        var updateCommand = new ProductUpdateCommand();
        when(conversionServiceMock.convert(updateRequest, ProductUpdateCommand.class)).thenReturn(updateCommand);

        ProductUpdateCommand result = sut.mapToServiceCommand(updateRequest);

        assertThat(result).isSameAs(updateCommand);
    }

    @Test
    void mapToServiceResponseShouldDelegateToConversionService() {
        when(conversionServiceMock.convert(productStub, ProductServiceResponse.class)).thenReturn(serviceResponseStub);

        ProductServiceResponse result = sut.mapToServiceResponse(productStub);

        assertThat(result).isSameAs(serviceResponseStub);
    }

    @Test
    void mapToControllerResponseShouldMapCorrectly() {
        GetProductResponse result = sut.mapToControllerResponse(serviceResponseStub, exchangeServiceResponseStub);

        assertThat(result.id()).isEqualTo(serviceResponseStub.id());
        assertThat(result.price()).isEqualTo(exchangeServiceResponseStub.price());
        assertThat(result.currency()).isEqualTo(exchangeServiceResponseStub.currency());
        assertThat(result.name()).isEqualTo(serviceResponseStub.name());
    }

    @Test
    void mapToControllerResponseForPageShouldMapCorrectly() {
        var page = new PageImpl<>(List.of(serviceResponseStub), PageRequest.of(0, 1), 1);
        var exchanges = List.of(exchangeServiceResponseStub);

        GetPageProductResponse<GetProductResponse> result = sut.mapToControllerResponse(page, exchanges);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(serviceResponseStub.id());
        assertThat(result.getTotalCount()).isEqualTo(page.getTotalElements());
        assertThat(result.getCurrentPage()).isEqualTo(page.getNumber());
    }
}
