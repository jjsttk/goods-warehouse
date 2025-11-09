package com.jjsttk.goodswarehouse.exception.service.exchange.deserializer;

public class ExchangeRateParsingException extends RuntimeException {
    public static final String EXCEPTION_MESSAGE = "Failed to parse exchange rates JSON";

    public ExchangeRateParsingException(Throwable cause) {
        super(EXCEPTION_MESSAGE, cause);
    }
}
