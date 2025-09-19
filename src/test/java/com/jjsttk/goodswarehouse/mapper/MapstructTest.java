package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import com.jjsttk.goodswarehouse.persistence.entity.ProductEntity;
import com.jjsttk.goodswarehouse.testutil.ProductTestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MapstructTest {
    private final MapstructProductMapper mapper = Mappers.getMapper(MapstructProductMapper.class);


    @Test
    void mapToGetPageProductResponseShouldMapAllFieldsCorrectly() {
        List<ProductEntity> products = ProductTestDataFactory.getProductsList(5);
        List<ProductServiceResponse> serviceResponses = ProductTestDataFactory.getServiceResponsesList(products);
        Page<ProductServiceResponse> page =
                new PageImpl<>(serviceResponses, PageRequest.of(0, 5),
                        serviceResponses.size());

        GetPageProductResponse<GetProductResponse> result = mapper.mapToControllerResponse(page);

        assertThat(result).isNotNull();
        assertThat(result.getTotalCount()).isEqualTo(page.getTotalElements());
        assertThat(result.getPageSize()).isEqualTo(page.getSize());
        assertThat(result.getCurrentPage()).isEqualTo(page.getNumber());
        assertThat(result.getCurrentPageSize()).isEqualTo(page.getNumberOfElements());
        assertThat(result.getTotalPages()).isEqualTo(page.getTotalPages());


        assertThat(result.getContent())
                .hasSize(serviceResponses.size())
                .extracting(GetProductResponse::id)
                .containsExactlyElementsOf(
                        serviceResponses.stream().map(ProductServiceResponse::id).toList());
    }
}
