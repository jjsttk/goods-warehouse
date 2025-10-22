package com.jjsttk.goodswarehouse.mapper;

import com.jjsttk.goodswarehouse.controller.response.GetPageProductResponse;
import com.jjsttk.goodswarehouse.controller.response.GetProductResponse;
import com.jjsttk.goodswarehouse.service.exchange.response.ExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.response.ProductServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * MapStruct mapper for converting {@link ProductServiceResponse} objects
 * to {@link GetProductResponse} and {@link GetPageProductResponse} DTOs
 * used in controller responses.
 * <p>
 * Supports mapping single products as well as paginated lists of products
 * with corresponding exchange rate adjustments.
 * <p>
 * Null values are ignored during mapping to prevent overwriting existing fields.
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class MapstructProductMapper implements ProductConverter {
    /**
     * Maps a single {@link ProductServiceResponse} and corresponding
     * {@link ExchangeServiceResponse} into a {@link GetProductResponse}.
     * <p>
     * The product price and currency are set from the exchange response.
     *
     * @param productServiceResponse the source product service response
     * @param exchangeServiceResponse the exchange service response with converted price
     * @return the controller response DTO with price and currency applied
     */
    @Override
    @Mapping(source = "exchangeServiceResponse.currency", target = "currency")
    @Mapping(source = "exchangeServiceResponse.price", target = "price")
    public abstract GetProductResponse mapToControllerResponse(
            ProductServiceResponse productServiceResponse,
            ExchangeServiceResponse exchangeServiceResponse);


    /**
     * Converts a page of {@link ProductServiceResponse} into
     * a {@link GetPageProductResponse} containing {@link GetProductResponse} objects.
     * <p>
     * Each product in the page is paired with the corresponding {@link ExchangeServiceResponse}
     * by index to apply price conversion and currency mapping.
     *
     * @param serviceResponse the page of service responses
     * @param exchangeServiceResponse the list of exchange service responses, aligned by index
     * @return a paginated controller response DTO with converted prices and currency
     */
    @Override
    public GetPageProductResponse<GetProductResponse> mapToControllerResponse(
            Page<ProductServiceResponse> serviceResponse,
            List<ExchangeServiceResponse> exchangeServiceResponse
    ) {
        List<GetProductResponse> content =
                mapList(serviceResponse.getContent(), exchangeServiceResponse);

        return GetPageProductResponse.<GetProductResponse>builder()
                .content(content)
                .totalCount(serviceResponse.getTotalElements())
                .totalPages(serviceResponse.getTotalPages())
                .currentPage(serviceResponse.getNumber())
                .pageSize(serviceResponse.getSize())
                .currentPageSize(serviceResponse.getNumberOfElements())
                .build();
    }

    /**
     * Helper method to map a list of {@link ProductServiceResponse} and
     * {@link ExchangeServiceResponse} objects to a list of {@link GetProductResponse}.
     * <p>
     * Assumes that both lists are aligned by index.
     *
     * @param products the list of products from the service
     * @param exchanges the corresponding list of exchange responses
     * @return a list of mapped controller response DTOs
     */
    private List<GetProductResponse> mapList(
            List<ProductServiceResponse> products,
            List<ExchangeServiceResponse> exchanges
    ) {
        return java.util.stream.IntStream.range(0, products.size())
                .mapToObj(i -> mapToControllerResponse(products.get(i), exchanges.get(i)))
                .toList();
    }
}
