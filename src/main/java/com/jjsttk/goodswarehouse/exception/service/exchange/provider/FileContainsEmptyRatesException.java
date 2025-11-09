package com.jjsttk.goodswarehouse.exception.service.exchange.provider;

public class FileContainsEmptyRatesException extends ExchangeRateProviderException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "File: %s, contains empty rates";

    public FileContainsEmptyRatesException(String fileName) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, fileName));
    }

    public FileContainsEmptyRatesException(String fileName, Throwable cause) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, fileName), cause);
    }
}
