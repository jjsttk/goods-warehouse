package com.jjsttk.goodswarehouse.service.exchange.provider;

import com.jjsttk.goodswarehouse.exception.service.exchange.provider.AllExchangeProvidersFailedException;
import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ExchangeRateProviderException;
import com.jjsttk.goodswarehouse.service.exchange.dto.request.ExchangeData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Primary exchange rate provider that implements fallback strategy.
 * Attempts to fetch rates from multiple providers in sequence until one succeeds.
 * If all providers fail, throws an exception with details of all failures.
 */
@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class PrimaryExchangeRateProvider implements ExchangeRateProvider {
    private static final String PROVIDER_NAME = "FALLBACK_STRATEGY";
    private static final String UNAVAILABLE_PROVIDER_FORMAT = "[%s] (unavailable)";
    private static final String FAILED_PROVIDER_FORMAT = "[%s] %s";

    private final List<ExchangeRateProvider> providers;

    /**
     * Attempts to fetch exchange rates from available providers in sequence.
     * Returns the first successful response or throws an exception if all providers fail.
     *
     * @return exchange data from the first successful provider
     * @throws ExchangeRateProviderException if all providers fail to return data
     */

    @Override
    public ExchangeData getExchangeData() {
        var providerErrorList = new ArrayList<String>();
        for (ExchangeRateProvider provider : providers) {
            if (!provider.isAvailable()) {
                log.debug("Provider {} is not available, skipping", provider.getProviderName());
                providerErrorList.add(
                        String.format(UNAVAILABLE_PROVIDER_FORMAT, provider.getProviderName())
                );
                continue;
            }

            try {
                log.debug("Attempting to fetch rates from provider: {}", provider.getProviderName());
                var data = provider.getExchangeData();
                log.debug("Successfully obtained rates from: {}", provider.getProviderName());
                return data;

            } catch (Exception e) {
                providerErrorList.add(
                        String.format(FAILED_PROVIDER_FORMAT, provider.getProviderName(), e.getMessage())
                );
                log.debug("Provider {} failed with error, message: {}, trying next available provider...",
                        provider.getProviderName(), e.getMessage());
            }
        }

        throw new AllExchangeProvidersFailedException(providerErrorList.toString());
    }

    /**
     * Returns the name of this provider strategy.
     *
     * @return provider name identifier
     */
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
