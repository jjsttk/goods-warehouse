package com.jjsttk.goodswarehouse.service.exchange.util.converter;

import com.jjsttk.goodswarehouse.service.exchange.util.converter.dto.request.PriceConverterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Default implementation of the {@link PriceConverter} interface.
 * <p>
 * This implementation performs price conversion by dividing the source price
 * by the actual exchange rate. The result is rounded to two decimal places
 * using the {@code HALF_UP} rounding mode.
 */
@Component
@RequiredArgsConstructor
public class DefaultPriceConverter implements PriceConverter {

    /**
     * Converts the source price using basic division logic.
     *
     * @param exchangeRequest the request containing the source price and the actual exchange rate.
     * @return a {@link BigDecimal} representing the converted price,
     * rounded to 2 decimal places (HALF_UP).
     * @throws ArithmeticException if the exchange rate is zero as a divider.
     */
    @Override
    public BigDecimal convert(PriceConverterRequest exchangeRequest) {
        return exchangeRequest.sourcePrice().divide(exchangeRequest.actualRate(), 2, RoundingMode.HALF_UP);
    }
}
