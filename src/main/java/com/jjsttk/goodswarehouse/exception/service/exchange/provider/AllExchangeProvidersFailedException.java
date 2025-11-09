package com.jjsttk.goodswarehouse.exception.service.exchange.provider;

public class AllExchangeProvidersFailedException extends ExchangeRateProviderException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "All ExchangeProviders failed. %s";

    public AllExchangeProvidersFailedException(String message) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, message));
    }
}
