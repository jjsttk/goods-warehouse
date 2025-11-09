package com.jjsttk.goodswarehouse.exception.service.exchange.deserializer;

import lombok.Getter;

@Getter
public class UnknownCurrencyException extends RuntimeException {
    public static final String EXCEPTION_MESSAGE_FORMAT = "Unknown currency: %s";
    private final String currency;

    public UnknownCurrencyException(String currencyStr) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, currencyStr));
        this.currency = currencyStr;
    }
}
