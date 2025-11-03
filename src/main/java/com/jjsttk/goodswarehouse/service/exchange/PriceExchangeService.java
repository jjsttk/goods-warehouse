package com.jjsttk.goodswarehouse.service.exchange;

import org.springframework.data.domain.Page;

/**
 * Generic service interface for currency exchange operations.
 *
 * @param <T> the input DTO type
 * @param <R> the response type for single operations
 */
public interface PriceExchangeService<T, R> {

    R exchange(T baseServiceDto);

    Page<R> exchange(Page<T> baseServiceDtoList);
}
