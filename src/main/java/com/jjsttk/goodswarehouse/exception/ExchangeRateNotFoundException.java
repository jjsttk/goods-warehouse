package com.jjsttk.goodswarehouse.exception;

import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import lombok.Getter;

@Getter
public class ExchangeRateNotFoundException extends RuntimeException {
    private final PriceCurrency currency;

    public ExchangeRateNotFoundException(PriceCurrency c) {
        super(String.format("Exchange rate not found for currency %s", c));
        this.currency = c;
    }
}
