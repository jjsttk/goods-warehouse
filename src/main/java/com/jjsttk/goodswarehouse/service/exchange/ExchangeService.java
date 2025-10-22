package com.jjsttk.goodswarehouse.service.exchange;

import com.jjsttk.goodswarehouse.service.exchange.response.ExchangeServiceResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for converting product prices to a target currency.
 * <p>
 * Implementations of this interface should handle fetching exchange rates,
 * applying currency conversion, and returning the results in a standardized
 * response object {@link ExchangeServiceResponse}.
 * <p>
 * For the default currency (e.g., {@link com.jjsttk.goodswarehouse.enums.PriceCurrency#RUB}),
 * the implementation may simply return the original price without conversion.
 */
public interface ExchangeService {

    /**
     * Converts the given price to the current session's target currency.
     * <p>
     * The target currency is typically provided
     * by a session-scoped {@link com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider}.
     * If the currency is the same as the original (e.g., RUB), the original price can be returned.
     *
     * @param price the original price in the base currency (RUB)
     * @return an {@link ExchangeServiceResponse} containing the converted price and currency
     */
    ExchangeServiceResponse exchange(BigDecimal price);

    /**
     * Converts the given list of prices to current session's target currency.
     * <p>
     * The target currency is typically provided
     * by a session-scoped {@link com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider}.
     * If the currency is the same as the original (e.g., RUB), the original price can be returned.
     *
     * @param prices the list of original prices in the base currency (RUB)
     * @return an {@link List<ExchangeServiceResponse>} containing the converted prices and currencies
     */
    List<ExchangeServiceResponse> exchange(List<BigDecimal> prices);
}
