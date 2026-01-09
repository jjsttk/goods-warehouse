package com.jjsttk.goodswarehouse.service.exchange.util.converter;

import com.jjsttk.goodswarehouse.service.exchange.util.converter.dto.request.PriceConverterRequest;

import java.math.BigDecimal;

/**
 * Interface for converting currency values and prices.
 * <p>
 * Provides a contract for implementing various currency exchange
 * and price calculation logic.
 */
public interface PriceConverter {
    /**
     * Converts a price based on the provided request parameters.
     *
     * @param exchangeRequest the object containing source price, and actual rate for calc.
     * @throws ArithmeticException if divider is {@code 0}
     * @return the converted price as a {@link BigDecimal}.
     */
    BigDecimal convert(PriceConverterRequest exchangeRequest);
}
