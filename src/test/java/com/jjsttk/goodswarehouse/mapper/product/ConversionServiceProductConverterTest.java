package com.jjsttk.goodswarehouse.mapper.product;

import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceCreateCommand;
import com.jjsttk.goodswarehouse.service.product.dto.command.ProductServiceUpdateCommand;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.BaseProductServiceDto;
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
    private ProductPriceExchangeServiceResponse productPriceExchangeServiceResponseStub;
    private GetProductResponse getProductResponseStub;
    private ProductServiceCreateCommand createCommandStub;
    private BaseProductServiceDto serviceResponseStub;

    @BeforeEach
    void setUp() {
        productStub = ProductTestDataFactory.getProductEntityWithoutGeneratedId();
        createCommandStub = ProductTestDataFactory.getProductCreateCommand(productStub);
        serviceResponseStub = ProductTestDataFactory.getProductServiceResponse(productStub);
        getProductResponseStub = ProductTestDataFactory.getGetProductResponse(productStub);

        productPriceExchangeServiceResponseStub =
                ProductTestDataFactory.getProductPriceExchangeServiceResponse(productStub);
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
        when(conversionServiceMock.convert(request, ProductServiceCreateCommand.class)).thenReturn(createCommandStub);

        ProductServiceCreateCommand result = sut.mapToServiceCommand(request);

        assertThat(result).isSameAs(createCommandStub);
    }

    @Test
    void mapToServiceCommandFromUpdateRequestShouldDelegateToConversionService() {
        var updateRequest = ProductTestDataFactory.getUpdateProductRequest(productStub);
        var updateCommand = new ProductServiceUpdateCommand();
        when(conversionServiceMock.convert(updateRequest, ProductServiceUpdateCommand.class)).thenReturn(updateCommand);

        ProductServiceUpdateCommand result = sut.mapToServiceCommand(updateRequest);

        assertThat(result).isSameAs(updateCommand);
    }

    @Test
    void mapToServiceResponseShouldDelegateToConversionService() {
        when(conversionServiceMock.convert(productStub, BaseProductServiceDto.class)).thenReturn(serviceResponseStub);

        BaseProductServiceDto result = sut.mapToServiceResponse(productStub);

        assertThat(result).isSameAs(serviceResponseStub);
    }

    @Test
    void mapToControllerResponseShouldMapCorrectly() {
        when(conversionServiceMock.convert(productPriceExchangeServiceResponseStub, GetProductResponse.class))
                .thenReturn(getProductResponseStub);

        GetProductResponse result = sut.mapToControllerResponse(productPriceExchangeServiceResponseStub);

        System.out.println("result: " + result);

        assertThat(result.id()).isEqualTo(serviceResponseStub.id());
        assertThat(result.price()).isEqualTo(productPriceExchangeServiceResponseStub.price());
        assertThat(result.currency()).isEqualTo(productPriceExchangeServiceResponseStub.currency());
        assertThat(result.name()).isEqualTo(serviceResponseStub.name());
    }

    @Test
    void mapToControllerResponseForPageShouldMapCorrectly() {
        var pageable = PageRequest.of(0, 1);
        var page = new PageImpl<>(
                List.of(serviceResponseStub),
                pageable,
                1
        );
        var pageExchangeServiceResponse = new PageImpl<>(
                List.of(productPriceExchangeServiceResponseStub),
                pageable,
                1
        );
        var pageGetProductResponse = ProductTestDataFactory.getGetPageProductResponse(
                pageable,
                List.of(productStub)
        );

        when(conversionServiceMock.convert(pageExchangeServiceResponse, PageGetProductResponse.class))
                .thenReturn(pageGetProductResponse);

        PageGetProductResponse<GetProductResponse> result = sut.mapToControllerResponse(pageExchangeServiceResponse);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().id()).isEqualTo(serviceResponseStub.id());
        assertThat(result.getTotalCount()).isEqualTo(page.getTotalElements());
        assertThat(result.getCurrentPage()).isEqualTo(page.getNumber());
    }
}
