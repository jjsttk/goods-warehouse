package com.jjsttk.goodswarehouse.exception.service.exchange.provider;

/**
 * Exception thrown when exchange rate data cannot be retrieved from a provider.
 */
public class ExchangeRateProviderException extends RuntimeException {

    public ExchangeRateProviderException(String message) {
        super(message);
    }

    public ExchangeRateProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
