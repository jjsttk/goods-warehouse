package com.jjsttk.goodswarehouse.exception.service.exchange.deserializer;

import com.jjsttk.goodswarehouse.shared.enums.PriceCurrency;
import lombok.Getter;

@Getter
public class ExchangeRateNotFoundException extends RuntimeException {
    public static final String EXCEPTION_MESSAGE_FORMAT = "Exchange rate not found for currency %s";
    private final PriceCurrency currency;

    public ExchangeRateNotFoundException(PriceCurrency c) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, c));
        this.currency = c;
    }
}
