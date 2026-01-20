package com.jjsttk.goodswarehouse.service.exchange.provider;

import com.jjsttk.goodswarehouse.shared.configuration.property.service.exchange.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ExchangeRateProviderException;
import com.jjsttk.goodswarehouse.exception.service.exchange.provider.FileContainsEmptyRatesException;
import com.jjsttk.goodswarehouse.exception.service.exchange.provider.ProviderRequestFailedException;
import com.jjsttk.goodswarehouse.service.exchange.dto.response.ExchangeData;
import com.jjsttk.goodswarehouse.shared.util.json.JsonResourceLoader;
import lombok.RequiredArgsConstructor;
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
public class FileBasedExchangeProvider implements ExchangeDataProvider {
    private static final String PROVIDER_NAME = "FILE_BASED";
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
     *                                       or contains empty rates data
     */
    @Override
    public ExchangeData getExchangeData() {
        var fallbackFileName = properties.getFallbackFile();

        try {
            var result = jsonResourceLoader.loadObject(fallbackFileName, ExchangeData.class);

            if (result.rates() == null || result.rates().isEmpty()) {
                throw new FileContainsEmptyRatesException(fallbackFileName);
            }

            return result;

        } catch (Exception e) {
            throw new ProviderRequestFailedException(e.getMessage(), e);
        }
    }

    /**
     * Returns the identifier for this file-based exchange rate provider.
     *
     * @return the provider name identifier
     */
    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }
}
