package com.jjsttk.goodswarehouse.service.exchange.dto.request.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.service.exchange.dto.request.deserializer.ExchangeDataDeserializer;
import com.jjsttk.goodswarehouse.shared.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.exception.service.exchange.deserializer.ExchangeRateParsingException;
import com.jjsttk.goodswarehouse.exception.service.exchange.deserializer.UnknownCurrencyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExchangeDataDeserializerTest {

    private ExchangeDataDeserializer deserializer;
    private ObjectMapper objectMapper;

    @Mock
    private DeserializationContext deserializationContext;

    @BeforeEach
    void setUp() {
        deserializer = new ExchangeDataDeserializer();
        objectMapper = new ObjectMapper();
    }

    @Test
    void deserializeShouldParseValidJsonWithAllCurrencies() throws Exception {
        var json = """
                {
                    "exchangeRateUSD": 1.0,
                    "exchangeRateEUR": 0.85,
                    "exchangeRateCNY": 7.23,
                    "otherField": "will be ignored"
                }
                """;
        var parser = objectMapper.createParser(json);

        var result = deserializer.deserialize(parser, deserializationContext);


        assertThat(result).isNotNull();
        assertThat(result.rates()).hasSize(3);
        assertThat(result.rates())
                .containsKeys(PriceCurrency.USD, PriceCurrency.EUR, PriceCurrency.CNY);
        assertThat(result.rates().get(PriceCurrency.USD)).isEqualByComparingTo("1.0");
        assertThat(result.rates().get(PriceCurrency.EUR)).isEqualByComparingTo("0.85");
        assertThat(result.rates().get(PriceCurrency.CNY)).isEqualByComparingTo("7.23");
    }

    @Test
    void deserializeShouldParseJsonWithSingleCurrency() throws Exception {
        var json = """
                {
                    "exchangeRateUSD": 1.0
                }
                """;
        var parser = objectMapper.createParser(json);

        var result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result).isNotNull();
        assertThat(result.rates()).hasSize(1);
        assertThat(result.rates()).containsKey(PriceCurrency.USD);
        assertThat(result.rates().get(PriceCurrency.USD)).isEqualByComparingTo("1.0");
    }

    @Test
    void deserializeShouldIgnoreNonExchangeRateFields() throws Exception {
        var json = """
                {
                    "exchangeRateUSD": 1.0,
                    "timestamp": "2024-01-01",
                    "baseCurrency": "USD",
                    "someOtherField": 123
                }
                """;
        var parser = objectMapper.createParser(json);

        var result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result).isNotNull();
        assertThat(result.rates()).hasSize(1);
        assertThat(result.rates()).containsOnlyKeys(PriceCurrency.USD);
    }

    @Test
    void deserializeShouldHandleDecimalValues() throws Exception {
        var json = """
                {
                    "exchangeRateUSD": 1.0,
                    "exchangeRateEUR": 0.851234,
                    "exchangeRateCNY": 7.234567
                }
                """;
        JsonParser parser = objectMapper.createParser(json);

        var result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result).isNotNull();
        assertThat(result.rates()).containsEntry(PriceCurrency.EUR, BigDecimal.valueOf(0.851234));
        assertThat(result.rates()).containsEntry(PriceCurrency.CNY, BigDecimal.valueOf(7.234567));
    }

    @Test
    void deserializeShouldHandleIntegerValues() throws Exception {
        var json = """
                {
                    "exchangeRateUSD": 1,
                    "exchangeRateEUR": 85
                }
                """;
        var parser = objectMapper.createParser(json);

        var result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result).isNotNull();
        assertThat(result.rates()).containsEntry(PriceCurrency.USD, BigDecimal.valueOf(1));
        assertThat(result.rates()).containsEntry(PriceCurrency.EUR, BigDecimal.valueOf(85));
    }

    @Test
    void deserializeShouldThrowUnknownCurrencyExceptionWhenCurrencyCodeIsInvalid() throws Exception {
        var json = """
                {
                    "exchangeRateINVALID": 1.0
                }
                """;
        try (var parser = objectMapper.createParser(json)) {

            assertThatThrownBy(() -> deserializer.deserialize(parser, deserializationContext))
                    .isInstanceOf(UnknownCurrencyException.class)
                    .hasMessageContaining("INVALID");
        }
    }

    @Test
    void deserializeShouldThrowExchangeRateParsingExceptionWhenJsonIsInvalid() throws Exception {
        var invalidJson = "{ invalid json }";
        try (var parser = objectMapper.createParser(invalidJson)) {

            assertThatThrownBy(() -> deserializer.deserialize(parser, deserializationContext))
                    .isInstanceOf(ExchangeRateParsingException.class)
                    .hasCauseInstanceOf(IOException.class);
        }
    }

    @Test
    void deserializeShouldHandleEmptyJsonObject() throws Exception {
        var json = "{}";
        var parser = objectMapper.createParser(json);

        var result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result).isNotNull();
        assertThat(result.rates()).isEmpty();
    }

    @Test
    void deserializeShouldHandleZeroValues() throws Exception {
        var json = """
                {
                    "exchangeRateUSD": 0.0,
                    "exchangeRateEUR": 0
                }
                """;
        var parser = objectMapper.createParser(json);

        var result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result).isNotNull();
        assertThat(result.rates()).containsEntry(PriceCurrency.USD, new BigDecimal("0.0"));
        assertThat(result.rates()).containsEntry(PriceCurrency.EUR, BigDecimal.ZERO);
    }

    @Test
    void deserializeShouldHandleNegativeValues() throws Exception {
        var json = """
                {
                    "exchangeRateUSD": -1.0,
                    "exchangeRateEUR": -0.85
                }
                """;
        var parser = objectMapper.createParser(json);

        var result = deserializer.deserialize(parser, deserializationContext);

        assertThat(result).isNotNull();
        assertThat(result.rates()).containsEntry(PriceCurrency.USD, BigDecimal.valueOf(-1.0));
        assertThat(result.rates()).containsEntry(PriceCurrency.EUR, BigDecimal.valueOf(-0.85));
    }
}
