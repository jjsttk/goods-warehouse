package com.jjsttk.goodswarehouse.service.exchange;

import com.jjsttk.goodswarehouse.configuration.property.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.exception.ExchangeRateNotFoundException;
import com.jjsttk.goodswarehouse.service.exchange.client.ExchangeRateClient;
import com.jjsttk.goodswarehouse.service.exchange.client.ExchangeRateClientImpl;
import com.jjsttk.goodswarehouse.service.exchange.currency.fallback.ExchangeFallbackLoader;
import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import com.jjsttk.goodswarehouse.service.exchange.response.ExchangeServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Service for converting product prices to the currently selected currency.
 * <p>
 * This implementation uses {@link CurrencyProvider} to determine the target currency
 * for the current session. If the target currency is {@link PriceCurrency#RUB},
 * no conversion is performed and the original price is returned.
 * <p>
 * For other currencies, the service fetches exchange rates from an external API
 * via {@link ExchangeRateClientImpl}. If the API is unavailable, a fallback
 * JSON file is used. The service retries fetching from the server according to
 * {@link ExchangeServiceProperties#getRetryAttempts()}.
 * <p>
 * Conversion results are returned as {@link ExchangeServiceResponse}. Methods
 * support both single price conversion and batch conversion for a list of prices.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final CurrencyProvider provider;
    private final ExchangeRateClient exchangeRateClient;
    private final ExchangeServiceProperties properties;
    private final ExchangeFallbackLoader exchangeFallbackLoader;

    /**
     * Converts a single price to the current session currency.
     * <p>
     * If the currency is {@link PriceCurrency#RUB}, the original price is returned
     * without invoking external services.
     *
     * @param price the original price in RUB
     * @return converted price wrapped in {@link ExchangeServiceResponse}
     */
    @Override
    public ExchangeServiceResponse exchange(BigDecimal price) {
        var currency = provider.getCurrency();
        if (currency == PriceCurrency.RUB) {
            return buildResponse(currency, price);
        }

        var exchangeData = fetchExchangeData();

        var rate = getRateForCurrency(currency, exchangeData);
        var convertedPrice = convertPrice(price, rate);

        return buildResponse(currency, convertedPrice);
    }

    /**
     * Converts a list of prices to the current session currency.
     * <p>
     * If the currency is {@link PriceCurrency#RUB}, original prices are returned
     * as-is without fetching exchange rates.
     *
     * @param prices list of prices in RUB
     * @return list of converted prices wrapped in {@link ExchangeServiceResponse}
     */
    public List<ExchangeServiceResponse> exchange(List<BigDecimal> prices) {
        if (!prices.isEmpty()) {
            var currency = provider.getCurrency();
            if (currency == PriceCurrency.RUB) {
                return prices.stream()
                        .map(p -> buildResponse(currency, p))
                        .toList();
            }

            var exchangeData = fetchExchangeData();

            var rate = getRateForCurrency(currency, exchangeData);
            return prices.stream()
                    .map(p -> buildResponse(currency, convertPrice(p, rate)))
                    .toList();
        }
        return List.of();
    }


    /**
     * Fetches exchange data from the server with retry. If all attempts fail,
     * fallback to the local JSON file specified in {@link ExchangeServiceProperties#getFallbackFile()}.
     *
     * @return exchange data for all supported currencies
     */
    private ExchangeData fetchExchangeData() {
        return Optional.ofNullable(fetchFromServerWithRetry())
                .orElseGet(() -> {
                    log.info("Server unavailable, using local fallback from file {}", properties.getFallbackFile());
                    return fetchFromLocal();
                });
    }

    /**
     * Tries to fetch exchange rates from the external API with retry logic.
     *
     * @return exchange data or {@code null} if all attempts failed
     */
    private ExchangeData fetchFromServerWithRetry() {
        var maxAttempts = properties.getRetryAttempts();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                log.info("Fetching exchange rates (attempt {}/{})", attempt, maxAttempts);
                var data = exchangeRateClient.getExchangeData();
                log.info("Successfully fetched exchange rates from server");
                return data;

            } catch (Exception e) {
                log.warn("Attempt {}/{} failed: {}", attempt, maxAttempts, e.getMessage());
            }
        }

        log.warn("All {} attempts failed to fetch rates from server", maxAttempts);
        return null;
    }

    /**
     * Loads exchange data from a local fallback JSON file.
     *
     * @return exchange data from local file
     * @throws RuntimeException if the fallback file cannot be read
     */
    private ExchangeData fetchFromLocal() {
        return exchangeFallbackLoader.getFallBackDataFromFile(properties.getFallbackFile());
    }

    /**
     * Returns the exchange rate for a specific currency from the provided exchange data.
     *
     * @param currency the target currency
     * @param data     exchange data containing rates
     * @return the conversion rate
     * @throws ExchangeRateNotFoundException if the currency is not found
     */
    private BigDecimal getRateForCurrency(PriceCurrency currency, ExchangeData data) {
        return Optional.ofNullable(data.rates().get(currency))
                .orElseThrow(() -> new ExchangeRateNotFoundException(currency));
    }

    /**
     * Converts a price using the specified exchange rate.
     *
     * @param price the original price
     * @param rate  the exchange rate
     * @return the converted price
     */
    private BigDecimal convertPrice(BigDecimal price, BigDecimal rate) {
        return price.divide(rate, 2, RoundingMode.HALF_UP);
    }

    /**
     * Builds an {@link ExchangeServiceResponse} containing the currency and price.
     *
     * @param currency       the currency
     * @param convertedPrice the price in the given currency
     * @return response containing currency and converted price
     */
    private ExchangeServiceResponse buildResponse(PriceCurrency currency, BigDecimal convertedPrice) {
        return ExchangeServiceResponse.builder()
                .currency(currency)
                .price(convertedPrice)
                .build();
    }
}
