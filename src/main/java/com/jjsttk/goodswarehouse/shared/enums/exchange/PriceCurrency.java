package com.jjsttk.goodswarehouse.shared.enums.exchange;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;

public enum PriceCurrency {
    CNY,
    EUR,
    RUB,
    USD,
    BGN;

    @JsonCreator
    public static @Nullable PriceCurrency fromValue(@Nullable String str) {
        return str == null ? null : PriceCurrency.valueOf(str.strip().toUpperCase());
    }
}
