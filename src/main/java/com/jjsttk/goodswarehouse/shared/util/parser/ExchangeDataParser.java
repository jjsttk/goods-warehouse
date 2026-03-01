package com.jjsttk.goodswarehouse.shared.util.parser;

import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeData;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class ExchangeDataParser {
    public static ExchangeData convert(Map<String, BigDecimal> src) {
        var rates = new HashMap<PriceCurrency, BigDecimal>();

        src.forEach((key, value) -> {
            if (key.startsWith("exchangeRate")) {
                var currencyCode = key.substring("exchangeRate".length());
                var currency = PriceCurrency.fromValue(currencyCode);
                if (currency != null) {
                    rates.put(currency, value);
                }
            }
        });

        return new ExchangeData(rates);
    }
}
