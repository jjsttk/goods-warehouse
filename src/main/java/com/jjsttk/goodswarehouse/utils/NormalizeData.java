package com.jjsttk.goodswarehouse.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class NormalizeData {

    public static String normalizeString(String value) {
        return value == null ? null : value.strip().replaceAll("\\s+", " ");
    }

    public static String normalizeCategory(String category) {
        return category == null ? null : category.trim().toUpperCase();
    }

    public static BigDecimal normalizePrice(BigDecimal price) {
        return price == null ? null: price.setScale(2, RoundingMode.HALF_UP);
    }

}
