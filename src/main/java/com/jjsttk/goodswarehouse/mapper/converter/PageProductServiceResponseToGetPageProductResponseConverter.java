package com.jjsttk.goodswarehouse.mapper.converter;

import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.response.ProductServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public final class PageProductServiceResponseToGetPageProductResponseConverter
        implements Converter<Page<ProductServiceResponse>, GetPageProductResponse<GetProductResponse>> {

    private final ProductServiceResponseDtoToProductResponseDtoConverter converter;

    @Override
    public GetPageProductResponse<GetProductResponse> convert(Page<ProductServiceResponse> source) {
        return GetPageProductResponse.<GetProductResponse>builder()
                .content(source.getContent().stream().map(converter::convert).toList())
                .totalCount(source.getTotalElements())
                .totalPages(source.getTotalPages())
                .currentPage(source.getNumber())
                .pageSize(source.getSize())
                .currentPageSize(source.getNumberOfElements())
                .build();
    }
}
