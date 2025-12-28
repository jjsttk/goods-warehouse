package com.jjsttk.goodswarehouse.service.exchange.currency.filter;

import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private CurrencyFilter sut;

    @Test
    void doFilterInternalShouldSetCurrencyWhenValidCurrencyHeaderProvided() throws Exception {
        // Given
        when(request.getHeader("currency")).thenReturn("USD");

        sut.doFilterInternal(request, response, filterChain);

        verify(currencyProvider).setCurrency(PriceCurrency.USD);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldSetCurrencyWhenValidCurrencyHeaderInLowerCase() throws Exception {
        when(request.getHeader("currency")).thenReturn("eur");

        sut.doFilterInternal(request, response, filterChain);

        verify(currencyProvider).setCurrency(PriceCurrency.EUR);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldSetCurrencyWhenValidCurrencyHeaderWithSpaces() throws Exception {
        when(request.getHeader("currency")).thenReturn("  CNY  ");

        sut.doFilterInternal(request, response, filterChain);

        verify(currencyProvider).setCurrency(PriceCurrency.CNY);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldContinueFilterChainWhenNoCurrencyHeader() throws Exception {
        when(request.getHeader("currency")).thenReturn(null);

        sut.doFilterInternal(request, response, filterChain);

        verify(currencyProvider, never()).setCurrency(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternalShouldContinueFilterChainWhenEmptyCurrencyHeader() throws Exception {
        when(request.getHeader("currency")).thenReturn("");

        sut.doFilterInternal(request, response, filterChain);

        verify(currencyProvider, never()).setCurrency(any());
        verify(filterChain).doFilter(request, response);
    }

    // Test all supported currencies
    @Test
    void doFilterInternalShouldHandleAllSupportedCurrencies() throws Exception {
        for (PriceCurrency currency : PriceCurrency.values()) {
            // Reset mocks for each iteration
            reset(request, currencyProvider, filterChain);

            when(request.getHeader("currency")).thenReturn(currency.name());

            sut.doFilterInternal(request, response, filterChain);

            verify(currencyProvider).setCurrency(currency);
            verify(filterChain).doFilter(request, response);
        }
    }

    @Test
    void doFilterInternalShouldNotSetCurrencyWhenBlankCurrencyHeader() throws Exception {
        when(request.getHeader("currency")).thenReturn("   ");

        sut.doFilterInternal(request, response, filterChain);

        verify(currencyProvider, never()).setCurrency(any());
        verify(filterChain).doFilter(request, response);
    }
}
