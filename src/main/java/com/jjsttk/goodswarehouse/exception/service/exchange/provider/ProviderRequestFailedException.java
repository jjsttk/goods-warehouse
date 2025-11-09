package com.jjsttk.goodswarehouse.exception.service.exchange.provider;

public class ProviderRequestFailedException extends ExchangeRateProviderException {
    private static final String EXCEPTION_MESSAGE = "Exchange rate request failed.";
    private static final String DETAILED_EXCEPTION_MESSAGE_FORMAT = "Exchange rate request failed. Details: %s";

    public ProviderRequestFailedException() {
        super(EXCEPTION_MESSAGE);
    }

    public ProviderRequestFailedException(String message) {
        super(String.format(DETAILED_EXCEPTION_MESSAGE_FORMAT, message));
    }

    public ProviderRequestFailedException(String message, Throwable cause) {
        super(String.format(DETAILED_EXCEPTION_MESSAGE_FORMAT, message), cause);
    }

    public ProviderRequestFailedException(Throwable cause) {
        super(EXCEPTION_MESSAGE, cause);
    }
}
