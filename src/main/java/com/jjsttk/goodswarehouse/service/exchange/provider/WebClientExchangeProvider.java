package com.jjsttk.goodswarehouse.service.exchange.provider;

import com.jjsttk.goodswarehouse.configuration.property.service.exchange.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ExchangeRateProviderException;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

/**
 * Fetches exchange rates from an external currency API using {@link WebClient}.
 * <p>
 * This is the primary exchange rate provider that makes actual HTTP requests to
 * external services. It includes retry mechanisms for network timeouts and server
 * errors, and supports caching to improve performance and reduce external API calls.
 * </p>
 * <p>
 * The provider is conditionally enabled based on the property
 * {@code app.exchange-rate-client.implementation} with value "real". It is ordered
 * with highest priority and will be attempted first in the provider chain.
 * </p>
 * <p>
 * Exchange rates are cached using with cache. The cache
 * is skipped if the result is null or contains empty rates.
 * </p>
 *
 * @see WebClient
 * @see ExchangeServiceProperties
 * @see ExchangeRateProviderException
 */
@Component
@Order(1)
@ConditionalOnProperty(
        name = "app.exchange-rate-client.implementation",
        havingValue = "real",
        matchIfMissing = true
)
@Slf4j
@RequiredArgsConstructor
public class WebClientExchangeProvider implements ExchangeRateProvider {
    private final WebClient exchangeServiceWebClient;
    private final ExchangeServiceProperties props;

    /**
     * Fetches exchange rates from the external API and deserializes the response.
     * <p>
     * Makes an HTTP GET request to the configured API endpoint and parses the JSON
     * response into {@link ExchangeData}. Includes specific error handling for
     * read timeouts and other WebClient request exceptions.
     * </p>
     * <p>
     * Results are cached to avoid repeated API calls. The cache is bypassed if
     * the result is null or contains empty rates map.
     * </p>
     *
     * @return the exchange data containing currency rates
     * @throws ExchangeRateProviderException if the API request fails, times out,
     *         returns empty response, or response cannot be parsed
     * @see Cacheable
     */
    @Override
    @Cacheable(value = "exchangeRateClientCache", unless = "#result == null && #result.rates().empty")
    public ExchangeData getExchangeData() {
        log.info(
                "Fetching exchange rates from external API via WebClient."
                        + " This message visible only if real api request performs."
        );
        try {
            return exchangeServiceWebClient.get()
                    .uri(props.getMethods().getCurrencies())
                    .retrieve()
                    .bodyToMono(ExchangeData.class)
                    .blockOptional()
                    .orElseThrow(() -> new ExchangeRateProviderException("Received empty response from API"));

        } catch (WebClientRequestException e) {
            if (e.getCause() instanceof ReadTimeoutException) {
                var message = "Exchange rate request timed out after all retry attempts";
                log.error(message);
                throw new ExchangeRateProviderException(message, e);
            } else {
                var message = "Exchange rate request failed";
                log.error(message, e);
                throw new ExchangeRateProviderException(message, e);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ExchangeRateProviderException(e.getMessage(), e);
        }
    }

    /**
     * Returns the identifier for this WebClient-based exchange rate provider.
     *
     * @return the provider name identifier
     */
    @Override
    public String getProviderName() {
        return "WEB_CLIENT_API";
    }
}
