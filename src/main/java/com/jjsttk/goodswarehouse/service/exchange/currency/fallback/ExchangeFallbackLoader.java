package com.jjsttk.goodswarehouse.service.exchange.currency.fallback;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Loads fallback exchange rate data from JSON files when external services are unavailable.
 * Provides backup currency conversion data from local classpath resources.
 */
@Component
@RequiredArgsConstructor
public class ExchangeFallbackLoader {
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    /**
     * Loads exchange rate data from a classpath JSON file.
     *
     * @param fileName the JSON file name in classpath
     * @return ExchangeData containing fallback exchange rates
     * @throws RuntimeException if file cannot be read or parsed
     */
    public ExchangeData getFallBackDataFromFile(String fileName) {
        try {
            var resource = resourceLoader.getResource("classpath:" + fileName);
            if (!resource.exists()) {
                throw new RuntimeException("Fallback file not found: " + fileName);
            }
            return objectMapper.readValue(resource.getInputStream(), ExchangeData.class);
        } catch (IOException e) {
            throw new RuntimeException("Cannot fetch exchange rates from fallback file: " + fileName, e);
        }
    }
}
