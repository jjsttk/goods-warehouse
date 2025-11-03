package com.jjsttk.goodswarehouse.exception.service.exchange.deserializer;

import lombok.Getter;

@Getter
public class ExchangeRateParsingException extends RuntimeException {

    public ExchangeRateParsingException(Throwable cause) {
        super("Failed to parse exchange rates JSON", cause);
    }
}
