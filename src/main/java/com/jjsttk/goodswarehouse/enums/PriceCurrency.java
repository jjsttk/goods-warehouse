package com.jjsttk.goodswarehouse.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;

public enum PriceCurrency {
    CNY,
    EUR,
    RUB,
    USD,
    BGN;

    @JsonCreator
    public static PriceCurrency fromValue(@Nullable String str) {
        return str == null ? null : PriceCurrency.valueOf(str.strip().toUpperCase());
    }
}
