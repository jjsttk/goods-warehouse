package com.jjsttk.goodswarehouse.service.exchange.currency.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.exception.response.ErrorResponse;
import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyFilterTest {

    @Mock
    private CurrencyProvider currencyProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private CurrencyFilter currencyFilter;
    private ObjectMapper objectMapper;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        currencyFilter = new CurrencyFilter(currencyProvider, objectMapper);
        responseWriter = new StringWriter();
    }

    @Test
    void doFilterInternalShouldSetCurrencyWhenValidCurrencyHeaderProvided() throws Exception {
        // Given
        when(request.getHeader("currency")).thenReturn("USD");

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(currencyProvider).setCurrency(PriceCurrency.USD);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldSetCurrencyWhenValidCurrencyHeaderInLowerCase() throws Exception {
        when(request.getHeader("currency")).thenReturn("eur");

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(currencyProvider).setCurrency(PriceCurrency.EUR);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldSetCurrencyWhenValidCurrencyHeaderWithSpaces() throws Exception {
        when(request.getHeader("currency")).thenReturn("  CNY  ");

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(currencyProvider).setCurrency(PriceCurrency.CNY);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldContinueFilterChainWhenNoCurrencyHeader() throws Exception {
        when(request.getHeader("currency")).thenReturn(null);

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(currencyProvider, never()).setCurrency(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldContinueFilterChainWhenEmptyCurrencyHeader() throws Exception {
        when(request.getHeader("currency")).thenReturn("");

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(currencyProvider, never()).setCurrency(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldReturnBadRequestWhenInvalidCurrencyHeader() throws Exception {
        when(request.getHeader("currency")).thenReturn("INVALID");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(currencyProvider, never()).setCurrency(any());
        verify(filterChain, never()).doFilter(request, response);

        var responseBody = responseWriter.toString();
        assertThat(responseBody).contains("Unknown currency: INVALID");
        assertThat(responseBody).contains("UnknownCurrencyException");
        assertThat(responseBody).contains("CurrencyFilter");
    }

    @Test
    void doFilterInternalShouldReturnBadRequestWhenUnknownCurrencyCode() throws Exception {
        when(request.getHeader("currency")).thenReturn("GBP");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json;charset=UTF-8");
        verify(currencyProvider, never()).setCurrency(any());
        verify(filterChain, never()).doFilter(request, response);

        String responseBody = responseWriter.toString();
        assertThat(responseBody).contains("Unknown currency: GBP");
    }

    // Test all supported currencies
    @Test
    void doFilterInternalShouldHandleAllSupportedCurrencies() throws Exception {
        for (PriceCurrency currency : PriceCurrency.values()) {
            // Reset mocks for each iteration
            reset(request, currencyProvider, filterChain);

            when(request.getHeader("currency")).thenReturn(currency.name());

            currencyFilter.doFilterInternal(request, response, filterChain);

            verify(currencyProvider).setCurrency(currency);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternalShouldReturnValidErrorResponseFormat() throws Exception {
        when(request.getHeader("currency")).thenReturn("INVALID");
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        currencyFilter.doFilterInternal(request, response, filterChain);

        var responseBody = responseWriter.toString();

        // Parse and validate the error response structure
        var errorResponse = objectMapper.readValue(responseBody, ErrorResponse.class);

        assertThat(errorResponse.message()).isEqualTo("Unknown currency: INVALID");
        assertThat(errorResponse.exception()).isEqualTo("UnknownCurrencyException");
        assertThat(errorResponse.source()).isEqualTo("CurrencyFilter");
        assertThat(errorResponse.dateTime()).isBeforeOrEqualTo(OffsetDateTime.now());
    }

    @Test
    void doFilterInternalShouldNormalizeCurrencyHeader() throws Exception {
        when(request.getHeader("currency")).thenReturn("  usd  ");

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(currencyProvider).setCurrency(PriceCurrency.USD);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldNotSetCurrencyWhenBlankCurrencyHeader() throws Exception {
        when(request.getHeader("currency")).thenReturn("   ");

        currencyFilter.doFilterInternal(request, response, filterChain);

        verify(currencyProvider, never()).setCurrency(any());
        verify(filterChain).doFilter(request, response);
    }
}
