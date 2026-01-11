package com.jjsttk.goodswarehouse.shared.util.price;

import com.jjsttk.goodswarehouse.shared.util.price.dto.request.PriceConverterRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;

public interface PriceConverter {

    /**
     * Converts the source price using basic division logic.
     *
     * @param exchangeContext the request containing the source price and the actual exchange rate.
     * @return a {@link BigDecimal} representing the converted price,
     * rounded to 2 decimal places (HALF_UP).
     * @throws ArithmeticException if the exchange rate is zero as a divider.
     */
    static BigDecimal convert(PriceConverterRequest exchangeContext) {
        return exchangeContext.sourcePrice().divide(exchangeContext.actualRate(), 2, RoundingMode.HALF_UP);
    }

}
