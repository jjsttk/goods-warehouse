package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.service.exchange.response.ExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

public class MapstructTest {
    private final MapstructProductMapper mapper = Mappers.getMapper(MapstructProductMapper.class);


    @Test
    void mapToGetPageProductResponseShouldMapAllFieldsCorrectly() {
        var products = ProductTestDataFactory.getProductsList(5);
        var serviceResponses = ProductTestDataFactory.getServiceResponsesList(products);
        var page = new PageImpl<>(serviceResponses, PageRequest.of(0, 5),
                serviceResponses.size());
        var exchangeServiceResponses = new ArrayList<ExchangeServiceResponse>();
        for (var p : serviceResponses) {
            exchangeServiceResponses.add(ExchangeServiceResponse.builder()
                    .price(p.price())
                    .currency(PriceCurrency.RUB)
                    .build()
            );
        }

        GetPageProductResponse<GetProductResponse> result = mapper.mapToControllerResponse(
                page, exchangeServiceResponses
        );

        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(page.getTotalElements());
        assertThat(result.getPageSize()).isEqualTo(page.getSize());
        assertThat(result.getCurrentPage()).isEqualTo(page.getNumber());
        assertThat(result.getCurrentPageSize()).isEqualTo(page.getNumberOfElements());
        assertThat(result.getTotalPages()).isEqualTo(page.getTotalPages());
        assertThat(result.getContent().getFirst().currency()).isEqualTo(PriceCurrency.RUB);


        assertThat(result.getContent())
                .hasSize(serviceResponses.size())
                .extracting(GetProductResponse::id)
                .containsExactlyElementsOf(
                        serviceResponses.stream().map(ProductServiceResponse::id).toList());
    }
}
