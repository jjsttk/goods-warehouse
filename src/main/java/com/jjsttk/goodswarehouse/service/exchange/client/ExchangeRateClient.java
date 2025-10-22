package com.jjsttk.goodswarehouse.service.exchange.client;

import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;

/**
 * Client interface for retrieving currency exchange rate data.
 * Defines the contract for obtaining current exchange rates from external sources.
 */
public interface ExchangeRateClient {

    /**
     * Fetches current exchange rate data from the configured data source.
     *
     * @return ExchangeData containing current exchange rates and metadata
     * @throws Exception if exchange rate data cannot be retrieved due to
     *         network issues, service unavailability, or data parsing errors
     */
    ExchangeData getExchangeData() throws Exception;
}
