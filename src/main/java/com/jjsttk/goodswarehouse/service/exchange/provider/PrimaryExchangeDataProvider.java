package com.jjsttk.goodswarehouse.service.exchange.provider;

import com.jjsttk.goodswarehouse.exception.service.exchange.provider.AllExchangeProvidersFailedException;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class acts as a Composite provider with a built-in failover strategy.
 * <p>
 * A primary exchange rate provider that coordinates a chain of providers to ensure high availability.
 * <p>
 * This implementation acts as a failover wrapper. It iterates through all registered
 * {@link ExchangeDataProvider} beans in their order of precedence, attempting to fetch
 * data from the first available and functional provider.
 * <p>
 * Marked as {@link Primary} to be the default choice for injection when multiple
 * provider implementations are present.
 */
@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class PrimaryExchangeDataProvider implements ExchangeDataProvider {
    private static final String PROVIDER_NAME = "FALLBACK_STRATEGY";
    private static final String UNAVAILABLE_PROVIDER_FORMAT = "[%s] (unavailable)";
    private static final String FAILED_PROVIDER_FORMAT = "[%s] %s";

    private final List<ExchangeDataProvider> providers;

    /**
     * Executes the failover strategy to obtain exchange rates.
     * <p>
     * Logic flow:
     * 1. Check if the provider is marked as available.
     * 2. Attempt to call {@code getExchangeData()}.
     * 3. If a provider fails or is unavailable, log the incident and proceed to the next candidate.
     * 4. If the end of the list is reached without success, throw an aggregated exception.
     *
     * @return {@link ExchangeData} from the first successful provider in the chain.
     * @throws AllExchangeProvidersFailedException if every provider in the list fails. Or if no providers are available.
     *                                             Includes a concatenated report of all failures.
     */
    @Override
    public ExchangeData getExchangeData() {
        var providerErrorList = new ArrayList<String>();
        for (ExchangeDataProvider provider : providers) {
            // Self-exclusion to prevent infinite recursion if this list contains the Primary provider itself
            if (provider == this) {
                continue;
            }

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
     * {@inheritDoc}
     */
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
