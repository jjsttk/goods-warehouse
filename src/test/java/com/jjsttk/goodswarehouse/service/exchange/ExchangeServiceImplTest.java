package com.jjsttk.goodswarehouse.service.exchange;

import com.jjsttk.goodswarehouse.configuration.property.ExchangeServiceProperties;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.exception.ExchangeRateNotFoundException;
import com.jjsttk.goodswarehouse.service.exchange.client.ExchangeRateClient;
import com.jjsttk.goodswarehouse.service.exchange.currency.fallback.ExchangeFallbackLoader;
import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import com.jjsttk.goodswarehouse.service.exchange.request.ExchangeData;
import com.jjsttk.goodswarehouse.service.exchange.response.ExchangeServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceImplTest {

    @Mock
    private CurrencyProvider currencyProviderMock;

    @Mock
    private ExchangeRateClient exchangeRateClientMock;

    @Mock
    private ExchangeFallbackLoader fallbackLoaderMock;

    private ExchangeServiceProperties propertiesStub;

    private ExchangeServiceImpl sut;

    @BeforeEach
    void setUp() {
        propertiesStub = new ExchangeServiceProperties();
        var apiProps = new ExchangeServiceProperties.Api();
        var endpoint = new ExchangeServiceProperties.Api.Endpoint();

        endpoint.setPath("/currencies");
        apiProps.setCurrencies(endpoint);
        apiProps.setHost("http://localhost:1234");

        propertiesStub.setApi(apiProps);
        propertiesStub.setRetryAttempts(3);
        propertiesStub.setFallbackFile("fallback.json");

        sut = new ExchangeServiceImpl(
                currencyProviderMock,
                exchangeRateClientMock,
                propertiesStub,
                fallbackLoaderMock
        );
    }

    @Test
    void exchangeShouldReturnSamePriceWhenCurrencyIsRUB() {
        var price = new BigDecimal("1000.50");
        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.RUB);

        var result = sut.exchange(price);

        assertThat(result).isNotNull();
        assertThat(result.currency()).isEqualTo(PriceCurrency.RUB);
        assertThat(result.price()).isEqualTo(price);
        verifyNoInteractions(exchangeRateClientMock);
    }

    @Test
    void exchangeShouldConvertPriceWhenCurrencyIsNotRUB() throws Exception {
        var price = new BigDecimal("1000.00");
        var exchangeData = ExchangeData.builder()
                .rates(Map.of(
                        PriceCurrency.USD, new BigDecimal("92.50"),
                        PriceCurrency.EUR, new BigDecimal("98.75"),
                        PriceCurrency.CNY, new BigDecimal("12.34")
                ))
                .build();


        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.USD);
        when(exchangeRateClientMock.getExchangeData()).thenReturn(exchangeData);

        var result = sut.exchange(price);

        assertThat(result).isNotNull();
        assertThat(result.currency()).isEqualTo(PriceCurrency.USD);
        assertThat(result.price()).isEqualByComparingTo("10.81"); // 1000 / 92.50 = 10.81
    }

    @Test
    void exchangeShouldThrowExceptionWhenCurrencyRateNotFound() throws Exception {
        var price = new BigDecimal("1000.00");
        var exchangeData = ExchangeData.builder()
                .rates(Map.of(
                        PriceCurrency.EUR, new BigDecimal("98.75"), // USD no data
                        PriceCurrency.CNY, new BigDecimal("12.34")
                ))
                .build();

        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.USD);
        when(exchangeRateClientMock.getExchangeData()).thenReturn(exchangeData);

        assertThatThrownBy(() -> sut.exchange(price))
                .isInstanceOf(ExchangeRateNotFoundException.class)
                .hasMessageContaining("USD");
    }

    @Test
    void exchangeListShouldReturnSamePricesWhenCurrencyIsRUB() {
        List<BigDecimal> prices = List.of(
                new BigDecimal("100.00"),
                new BigDecimal("200.50"),
                new BigDecimal("300.75")
        );

        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.RUB);

        var results = sut.exchange(prices);

        assertThat(results).hasSize(3);
        assertThat(results)
                .extracting(ExchangeServiceResponse::currency)
                .containsOnly(PriceCurrency.RUB);

        assertThat(results)
                .extracting(ExchangeServiceResponse::price)
                .containsExactly(new BigDecimal("100.00"), new BigDecimal("200.50"), new BigDecimal("300.75"));

        verifyNoInteractions(exchangeRateClientMock);
    }

    @Test
    void exchangeListShouldConvertAllPricesWhenCurrencyIsNotRUB() throws Exception {
        var prices = List.of(
                new BigDecimal("100.00"),
                new BigDecimal("200.00")
        );

        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.EUR);

        var exchangeData = ExchangeData.builder()
                .rates(Map.of(
                        PriceCurrency.USD, new BigDecimal("92.50"),
                        PriceCurrency.EUR, new BigDecimal("98.75"),
                        PriceCurrency.CNY, new BigDecimal("12.34")
                ))
                .build();

        when(exchangeRateClientMock.getExchangeData()).thenReturn(exchangeData);

        var results = sut.exchange(prices);

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(ExchangeServiceResponse::currency)
                .containsOnly(PriceCurrency.EUR);
        assertThat(results)
                .extracting(ExchangeServiceResponse::price)
                .containsExactly(new BigDecimal("1.01"), new BigDecimal("2.03")); // 100/98.75=1.01, 200/98.75=2.03
    }

    @Test
    void exchangeListShouldReturnEmptyListWhenInputIsEmpty() {
        var results = sut.exchange(List.of());

        assertThat(results).isEmpty();
        verifyNoInteractions(currencyProviderMock);
        verifyNoInteractions(exchangeRateClientMock);
    }

    @Test
    void exchangeShouldUseFallbackWhenServerFails() throws Exception {
        var price = new BigDecimal("1000.00");
        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.USD);

        // Server down
        when(exchangeRateClientMock.getExchangeData()).thenThrow(new RuntimeException("Server unavailable"));

        var fallbackData = ExchangeData.builder()
                .rates(Map.of(
                        PriceCurrency.USD, new BigDecimal("90.00"),
                        PriceCurrency.EUR, new BigDecimal("95.00")
                ))
                .build();

        when(fallbackLoaderMock.getFallBackDataFromFile(propertiesStub.getFallbackFile())).thenReturn(fallbackData);

        var result = sut.exchange(price);

        assertThat(result).isNotNull();
        assertThat(result.currency()).isEqualTo(PriceCurrency.USD);
        assertThat(result.price()).isEqualByComparingTo("11.11"); // 1000 / 90

        verify(exchangeRateClientMock, times(3)).getExchangeData();
        verify(fallbackLoaderMock, times(1)).getFallBackDataFromFile(propertiesStub.getFallbackFile());
    }

    @Test
    void exchangeShouldThrowRuntimeExceptionWhenFallbackFileFails() throws Exception {
        var price = new BigDecimal("1000.00");
        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.USD);

        // Server down
        when(exchangeRateClientMock.getExchangeData()).thenThrow(new RuntimeException("Server unavailable"));

        // Fallback file too down
        when(fallbackLoaderMock.getFallBackDataFromFile(propertiesStub.getFallbackFile()))
                .thenThrow(new RuntimeException("Cannot fetch exchange rates from server or fallback file"));

        assertThatThrownBy(() -> sut.exchange(price))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot fetch exchange rates from server or fallback file");
    }

    @Test
    void exchangeShouldHandleZeroPrice() throws Exception {
        var price = BigDecimal.ZERO;
        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.USD);

        var exchangeData = ExchangeData.builder()
                .rates(Map.of(PriceCurrency.USD, new BigDecimal("92.50")))
                .build();

        when(exchangeRateClientMock.getExchangeData()).thenReturn(exchangeData);

        var result = sut.exchange(price);

        assertThat(result).isNotNull();
        assertThat(result.price()).isEqualByComparingTo("0.00");
    }

    @Test
    void exchangeShouldUseCorrectRounding() throws Exception {
        var price = new BigDecimal("100.00");
        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.USD);

        var exchangeData = ExchangeData.builder()
                .rates(Map.of(PriceCurrency.USD, new BigDecimal("33.33")))
                .build();

        when(exchangeRateClientMock.getExchangeData()).thenReturn(exchangeData);

        var result = sut.exchange(price);

        assertThat(result).isNotNull();
        assertThat(result.price()).isEqualByComparingTo("3.00"); // 100 / 33.33 = 3.0003 → round to up 3.00
    }

    @Test
    void exchangeShouldCallServerOnlyOnceForMultipleConversions() throws Exception {
        var prices = List.of(
                new BigDecimal("100.00"),
                new BigDecimal("200.00"),
                new BigDecimal("300.00")
        );

        when(currencyProviderMock.getCurrency()).thenReturn(PriceCurrency.USD);

        var exchangeData = ExchangeData.builder()
                .rates(Map.of(PriceCurrency.USD, new BigDecimal("90.00")))
                .build();

        when(exchangeRateClientMock.getExchangeData()).thenReturn(exchangeData);

        var results = sut.exchange(prices);

        assertThat(results).hasSize(3);
        verify(exchangeRateClientMock, times(1)).getExchangeData();
    }
}
