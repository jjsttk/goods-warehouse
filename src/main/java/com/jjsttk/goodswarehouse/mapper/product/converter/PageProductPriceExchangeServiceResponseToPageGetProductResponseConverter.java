package com.jjsttk.goodswarehouse.mapper.product.converter;

import com.jjsttk.goodswarehouse.controller.product.dto.response.GetProductResponse;
import com.jjsttk.goodswarehouse.controller.product.dto.response.PageGetProductResponse;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public final class PageProductPriceExchangeServiceResponseToPageGetProductResponseConverter
        implements Converter<Page<ProductPriceExchangeServiceResponse>, PageGetProductResponse<GetProductResponse>> {

    private final ProductPriceExchangeServiceResponseToGetProductResponseConverter converter;

    @Override
    public PageGetProductResponse<GetProductResponse> convert(Page<ProductPriceExchangeServiceResponse> source) {
        var pageGetProductResponse = source.getContent().stream().map(converter::convert).toList();

        return new PageGetProductResponse<>(
                pageGetProductResponse,
                source.getTotalElements(),
                source.getTotalPages(),
                source.getNumber(),
                source.getSize(),
                source.getNumberOfElements()
        );
    }
}
