package com.jjsttk.goodswarehouse.service.exchange;

import com.jjsttk.goodswarehouse.service.exchange.provider.ExchangeRateProvider;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Default implementation of the {@link GlobalExchangeService} interface.
 * <p>
 * This service is responsible for retrieving current exchange rates,
 * leveraging an {@link ExchangeRateProvider} to fetch external data.
 * It treats {@link PriceCurrency#RUB} as the base currency with a rate of 1.0.
 */
@Service
@RequiredArgsConstructor
public class GlobalExchangeServiceImpl implements GlobalExchangeService {
    private final ExchangeRateProvider exchangeRateProvider;

    /**
     * Retrieves the actual exchange rate for a specified target currency relative to the base currency (RUB).
     * <p>
     * If the target currency is RUB, the rate is explicitly set to 1.0.
     * Otherwise, it fetches rates from the configured {@link ExchangeRateProvider}.
     *
     * @param targetCurrency the currency for which to retrieve the exchange rate.
     * @return the exchange rate as a {@link BigDecimal}. Returns {@code BigDecimal.ONE} as a fallback
     * if the specific rate is missing from the provider's data, or if the currency is RUB.
     */
    public BigDecimal getActualExchangeRateForCurrency(PriceCurrency targetCurrency) {
        if (targetCurrency == PriceCurrency.RUB) {
            return BigDecimal.ONE;
        }

        var exchangeData = exchangeRateProvider.getExchangeData();
        var rateForCurrency = exchangeData.rates().get(targetCurrency);

        return Optional.ofNullable(rateForCurrency)
                .orElse(BigDecimal.ONE);
    }
}
