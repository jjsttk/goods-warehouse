package com.jjsttk.goodswarehouse.service.exchange;

import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeData;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeRate;
import com.jjsttk.goodswarehouse.service.exchange.provider.ExchangeDataProvider;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service implementation for managing currency exchange operations within the application.
 * <p>
 * This service acts as a primary entry point for retrieving currency exchange rates.
 * It coordinates data from {@link CurrencyProvider} (to determine the user's current currency context)
 * and {@link ExchangeDataProvider} (to fetch actual market rates).
 * <p>
 * The service ensures that all price conversions throughout the warehouse system
 * stay consistent with the current session settings.
 */
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final CurrencyProvider currencyProvider;
    private final ExchangeDataProvider exchangeDataProvider;

    /**
     * Retrieves the exchange rate configuration for the current user session.
     * <p>
     * This method identifies the currency selected for the current session via {@link CurrencyProvider}
     * and then fetches the corresponding exchange rate relative to the base currency.
     *
     * @return an {@link ExchangeRate} containing the session currency and its current rate.
     * @see #getActualExchangeRateForCurrency(PriceCurrency)
     */
    public ExchangeRate getCurrentSessionExchangeRate() {
        var sessionCurrency = currencyProvider.getCurrency();
        var actualRate = getActualExchangeRateForCurrency(sessionCurrency);

        return buildResponse(sessionCurrency, actualRate);
    }

    /**
     * Retrieves the actual exchange rate for a specified target currency relative to the base currency (RUB).
     * <p>
     * If the target currency is RUB, the rate is explicitly set to 1.0.
     * Otherwise, it fetches rates from the configured {@link ExchangeDataProvider}.
     *
     * @param sessionCurrency the currency for which to retrieve the exchange rate.
     * @return the exchange rate as a {@link BigDecimal}. Returns {@code BigDecimal.ONE} as a fallback
     * if the specific rate is missing from the provider's data, or if the currency is RUB.
     */
    private BigDecimal getActualExchangeRateForCurrency(PriceCurrency sessionCurrency) {
        if (sessionCurrency == PriceCurrency.RUB) {
            return BigDecimal.ONE;
        }

        return Optional.ofNullable(exchangeDataProvider.getExchangeData())
                .map(ExchangeData::rates)
                .map(rates -> rates.get(sessionCurrency))
                .orElse(BigDecimal.ONE);
    }

    private ExchangeRate buildResponse(PriceCurrency targetCurrency, BigDecimal rateValue) {
        return ExchangeRate.builder()
                .currency(targetCurrency)
                .rate(rateValue)
                .build();
    }
}
