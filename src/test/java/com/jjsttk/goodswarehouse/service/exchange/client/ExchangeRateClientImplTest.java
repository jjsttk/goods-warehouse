package com.jjsttk.goodswarehouse.service.exchange.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.configuration.property.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateClientImplTest {

    @Mock
    private ObjectMapper objectMapper;

    private ExchangeRateClientImpl sut;

    @BeforeEach
    void setUp() {
        var properties = new ExchangeServiceProperties();

        var apiProps = new ExchangeServiceProperties.Api();
        var endpoint = new ExchangeServiceProperties.Api.Endpoint();

        endpoint.setPath("/currencies");
        apiProps.setCurrencies(endpoint);
        apiProps.setHost("http://localhost:1234");

        properties.setApi(apiProps);
        properties.setRetryAttempts(3);
        properties.setFallbackFile("fallback.json");

        sut = new ExchangeRateClientImpl(properties, objectMapper) {
            @Override
            protected String fetchJsonRates() {
                return """
                       {
                         "exchangeRateCNY": 16.86,
                         "exchangeRateEUR": 70.1,
                         "exchangeRateUSD": 50
                       }
                       """;
            }
        };
    }

    @Test
    void getExchangeDataShouldReturnParsedExchangeData() throws Exception {
        var json = """
                {
                  "exchangeRateCNY": 16.86,
                  "exchangeRateEUR": 70.1,
                  "exchangeRateUSD": 50
                }
                """;
        var expected = new ExchangeData(Map.of(
                PriceCurrency.CNY, new BigDecimal("16.86"),
                PriceCurrency.EUR, new BigDecimal("70.1"),
                PriceCurrency.USD, new BigDecimal("50")
        ));

        when(objectMapper.readValue(json, ExchangeData.class)).thenReturn(expected);

        var result = sut.getExchangeData();

        assertThat(result).isSameAs(expected);
        verify(objectMapper, times(1)).readValue(json, ExchangeData.class);
    }
}
