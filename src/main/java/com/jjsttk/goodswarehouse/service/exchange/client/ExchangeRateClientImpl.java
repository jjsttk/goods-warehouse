package com.jjsttk.goodswarehouse.service.exchange.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.configuration.property.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Implementation of {@link ExchangeRateClient} that fetches exchange rates
 * from an external currency API using {@link WebClient}.
 * <p>
 * The client supports caching of exchange rates to improve performance and
 * reduce the number of external HTTP requests.
 * <p>
 * The JSON response from the API is deserialized into {@link ExchangeData}
 * using {@link ObjectMapper}.
 */
@Component
@ConditionalOnProperty(
        name = "app.exchange-rate-client.implementation",
        havingValue = "real",
        matchIfMissing = true
)
@Slf4j
public class ExchangeRateClientImpl implements ExchangeRateClient {
    private final WebClient client;
    private final ObjectMapper om;
    private final ExchangeServiceProperties props;

    /**
     * Constructs an instance of {@code ExchangeRateClientImpl} with the given
     * service properties and object mapper.
     *
     * @param properties     the exchange service properties containing API host and path
     * @param objectMapper   the Jackson object mapper for parsing JSON responses
     */
    public ExchangeRateClientImpl(ExchangeServiceProperties properties, ObjectMapper objectMapper) {
        this.om = objectMapper;
        this.props = properties;
        this.client = WebClient.builder()
                .baseUrl(properties.getApi().getHost())
                .build();
    }

    /**
     * Fetches exchange rates from the external API and deserializes the response
     * into {@link ExchangeData}.
     * <p>
     * This method is annotated with {@link Cacheable} so that repeated calls
     * return cached data, avoiding unnecessary HTTP requests.
     * <p>
     * The cache key is derived from method parameters (none here) and the result
     * is cached only if it is non-null. Synchronous cache update is enabled.
     *
     * @return the exchange data containing currency rates
     * @throws Exception if there is an error fetching or parsing the API response
     */
    @Override
    @Cacheable(value = "rates")
    public ExchangeData getExchangeData() throws Exception {
        log.info("REAL API CALL");
        var json = fetchJsonRates();
        log.warn("Received JSON: {}", json);
        return om.readValue(json, ExchangeData.class);
    }

    /**
     * Performs the actual HTTP GET request to fetch the exchange rates as JSON.
     *
     * @return the raw JSON response from the currency API
     */
    protected String fetchJsonRates() {
        return client.get()
                .uri(props.getApi().getCurrencies().getPath())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
