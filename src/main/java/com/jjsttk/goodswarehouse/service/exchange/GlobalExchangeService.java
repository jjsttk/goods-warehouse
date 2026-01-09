package com.jjsttk.goodswarehouse.service.exchange;

import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;

import java.math.BigDecimal;

/**
 * Service interface for managing global currency exchange operations.
 * <p>
 * Provides methods to retrieve up-to-date exchange rates required
 * for price calculations and currency conversions across the system.
 */
public interface GlobalExchangeService {

    /**
     * Retrieves the current exchange rate for the specified currency.
     * <p>
     * The rate is typically calculated relative to a base currency (e.g., RUB).
     *
     * @param currency the target {@link PriceCurrency} to get the rate for.
     * @return the exchange rate as a {@link BigDecimal}.
     * @throws RuntimeException if the exchange rate data is unavailable
     *                          and no fallback is provided.
     */
    BigDecimal getActualExchangeRateForCurrency(PriceCurrency currency);
}
