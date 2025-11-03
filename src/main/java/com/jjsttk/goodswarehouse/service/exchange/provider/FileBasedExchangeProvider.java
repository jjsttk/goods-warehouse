package com.jjsttk.goodswarehouse.service.exchange.provider;

import com.jjsttk.goodswarehouse.configuration.property.service.exchange.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ExchangeRateProviderException;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import com.jjsttk.goodswarehouse.service.util.JsonResourceLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Loads fallback exchange rate data from JSON files when external services are unavailable.
 * <p>
 * This provider serves as a backup source for currency conversion data, loading rates
 * from local classpath resources. It is ordered with lower priority than real API providers
 * and is used when primary providers fail or are unavailable.
 * </p>
 * <p>
 * The JSON file location is configured via {@link ExchangeServiceProperties#getFallbackFile()}.
 * Validates that loaded rates are non-empty before returning the data.
 * </p>
 *
 * @see JsonResourceLoader
 * @see ExchangeServiceProperties
 * @see ExchangeRateProviderException
 */
@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class FileBasedExchangeProvider implements ExchangeRateProvider {
    private final JsonResourceLoader jsonResourceLoader;
    private final ExchangeServiceProperties properties;

    /**
     * Loads exchange rate data from the configured JSON file.
     * <p>
     * Attempts to load and parse the fallback JSON file from classpath. Validates that
     * the loaded data contains non-empty rates. If the file is missing, empty, or
     * contains invalid data, throws an {@link ExchangeRateProviderException}.
     * </p>
     *
     * @return the exchange data loaded from file
     * @throws ExchangeRateProviderException if the file cannot be loaded, parsed,
     *         or contains empty rates data
     */
    @Override
    public ExchangeData getExchangeData() {
        var fallbackFileName = properties.getFallbackFile();
        log.info("Loading exchange rates from file: {}", fallbackFileName);

        try {
            var result = jsonResourceLoader.loadObject(fallbackFileName, ExchangeData.class);

            if (result.rates() == null || result.rates().isEmpty()) {
                throw new ExchangeRateProviderException(
                        String.format("File: %s, contains empty rates.", fallbackFileName));
            }

            log.info("Successfully loaded {} rates from file {}", result.rates().size(), fallbackFileName);
            return result;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ExchangeRateProviderException(e.getMessage(), e);
        }
    }

    /**
     * Returns the identifier for this file-based exchange rate provider.
     *
     * @return the provider name identifier
     */
    @Override
    public String getProviderName() {
        return "FILE_BASED";
    }
}
