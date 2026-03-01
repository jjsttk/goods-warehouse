package com.jjsttk.goodswarehouse.service.exchange.provider;

import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ExchangeRateProviderException;
import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ProviderEmptyResponseException;
import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ProviderRequestFailedException;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeData;
import com.jjsttk.goodswarehouse.shared.configuration.property.rest.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.shared.util.parser.ExchangeDataParser;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Map;

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
@RequiredArgsConstructor
public class WebClientExchangeProvider implements ExchangeDataProvider {
    private static final String PROVIDER_NAME = "WEB_CLIENT_API";
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
     *                                       returns empty response
     * @see Cacheable
     */
    @Override
    @Cacheable(
            value = "rates-redis-cache",
            key = "'current-rates'",
            unless = "#result == null || #result.rates()?.isEmpty()"
    )
    public ExchangeData getExchangeData() {
        try {
            var srcMap =  exchangeServiceWebClient.get()
                    .uri(props.getEndpoints().getCurrencies())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, BigDecimal>>() {
                    })
                    .blockOptional()
                    .orElseThrow(() -> new ProviderEmptyResponseException(PROVIDER_NAME));

            return ExchangeDataParser.convert(srcMap);

        } catch (Exception e) {
            throw new ProviderRequestFailedException(e.getMessage(), e);
        }
    }

    /**
     * Returns the identifier for this WebClient-based exchange rate provider.
     *
     * @return the provider name identifier
     */
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
