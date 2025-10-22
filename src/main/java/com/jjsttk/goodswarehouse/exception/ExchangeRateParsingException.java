package com.jjsttk.goodswarehouse.exception;

import lombok.Getter;

@Getter
public class ExchangeRateParsingException extends RuntimeException {

    public ExchangeRateParsingException(Throwable cause) {
        super("Failed to parse exchange rates JSON", cause);
    }
}
