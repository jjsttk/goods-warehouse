package com.jjsttk.goodswarehouse.exception.service.exchange.provider;

public class ProviderEmptyResponseException extends ExchangeRateProviderException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Provider %s received empty response from API";

    public ProviderEmptyResponseException(String providerName) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, providerName));
    }

    public ProviderEmptyResponseException(String providerName, Throwable cause) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, providerName), cause);
    }
}
