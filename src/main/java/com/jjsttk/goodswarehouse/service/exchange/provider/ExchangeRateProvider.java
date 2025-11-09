package com.jjsttk.goodswarehouse.service.exchange.provider;

import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ExchangeRateProviderException;
import com.jjsttk.goodswarehouse.service.exchange.dto.request.ExchangeData;

/**
 * Provider interface for retrieving currency exchange rate data from various sources.
 * Defines the contract for obtaining current exchange rates from different data sources.
 */
public interface ExchangeRateProvider {

    /**
     * Fetches current exchange rate data from the provider's data source.
     *
     * @return ExchangeData containing current exchange rates and metadata
     * @throws ExchangeRateProviderException if exchange rate data cannot be retrieved
     */
    ExchangeData getExchangeData();

    /**
     * Returns the provider name for identification and logging.
     *
     * @return unique provider name
     */
    String getProviderName();

    /**
     * Checks if this provider is currently available and operational.
     *
     * @return true if provider is available, false otherwise
     */
    default boolean isAvailable() {
        return true;
    }
}
