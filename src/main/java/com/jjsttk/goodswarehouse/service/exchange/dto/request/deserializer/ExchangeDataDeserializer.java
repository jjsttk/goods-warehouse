package com.jjsttk.goodswarehouse.service.exchange.dto.request.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.exception.service.exchange.deserializer.ExchangeRateParsingException;
import com.jjsttk.goodswarehouse.exception.service.exchange.deserializer.UnknownCurrencyException;
import com.jjsttk.goodswarehouse.service.exchange.dto.request.ExchangeData;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

/**
 * Custom Jackson deserializer for {@link ExchangeData}.
 * <p>
 * This deserializer reads a JSON object containing exchange rates
 * with keys of the form "exchangeRate{CurrencyCode}" (e.g., "exchangeRateUSD").
 * It converts these entries into a {@link Map} from {@link PriceCurrency} to {@link BigDecimal}
 * and constructs an {@link ExchangeData} instance.
 * <p>
 * Known issues / behavior:
 * <ul>
 *     <li>If the JSON cannot be parsed, an {@link ExchangeRateParsingException} is thrown.</li>
 *     <li>If a currency code in the JSON does not match
 *     any {@link PriceCurrency}, an {@link UnknownCurrencyException} is thrown.</li>
 * </ul>
 */
public class ExchangeDataDeserializer extends JsonDeserializer<ExchangeData> {

    /**
     * Deserializes a JSON object into {@link ExchangeData}.
     *
     * @param p    the {@link JsonParser} used to read JSON content
     * @param ctxt the {@link DeserializationContext} for contextual information
     * @return an {@link ExchangeData} object containing exchange rates
     * @throws ExchangeRateParsingException if JSON cannot be parsed
     * @throws UnknownCurrencyException     if a currency code is unknown
     */
    @Override
    public ExchangeData deserialize(JsonParser p, DeserializationContext ctxt) {
        var node = readTreeOrThrow(p);
        Map<PriceCurrency, BigDecimal> map = new EnumMap<>(PriceCurrency.class);

        node.properties().forEach(entry -> {
            String key = entry.getKey();
            if (key.startsWith("exchangeRate")) {
                var shortCode = key.substring("exchangeRate".length());
                var currency = toCurrencyOrThrow(shortCode);
                map.put(currency, entry.getValue().decimalValue());
            }
        });

        return new ExchangeData(map);
    }

    private JsonNode readTreeOrThrow(JsonParser p) {
        try {
            return p.readValueAsTree();
        } catch (IOException e) {
            throw new ExchangeRateParsingException(e);
        }
    }

    private PriceCurrency toCurrencyOrThrow(String currencyCodeStr) {
        try {
            return PriceCurrency.fromValue(currencyCodeStr);
        } catch (Exception e) {
            throw new UnknownCurrencyException(currencyCodeStr);
        }
    }
}
