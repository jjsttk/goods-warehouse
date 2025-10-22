package com.jjsttk.goodswarehouse.exception;

import lombok.Getter;

@Getter
public class UnknownCurrencyException extends RuntimeException {
    private final String currency;

    public UnknownCurrencyException(String currencyStr) {
        super(String.format("Unknown currency: %s", currencyStr));
        this.currency = currencyStr;
    }
}
