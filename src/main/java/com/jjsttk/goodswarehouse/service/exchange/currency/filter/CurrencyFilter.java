package com.jjsttk.goodswarehouse.service.exchange.currency.filter;

import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts each HTTP request to extract the "currency" header
 * and set it into the {@link CurrencyProvider} for the current session.
 * <p>
 * This filter ensures that all downstream services and controllers can access
 * the selected currency from the session-scoped {@link CurrencyProvider}.
 * <p>
 * If the "currency" header is not present, the {@link CurrencyProvider} retains
 * its default value (typically RUB).
 * <p>
 * This filter extends {@link OncePerRequestFilter}, so it is invoked once per request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyFilter extends OncePerRequestFilter {
    private final CurrencyProvider currencyProvider;

    /**
     * Reads the "currency" header from the request and sets it into
     * the session-scoped {@link CurrencyProvider}.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException in case of a servlet error
     * @throws IOException      in case of an I/O error
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        var headerValue = request.getHeader("currency");

        if (headerValue != null && !headerValue.isBlank()) {
            try {
                currencyProvider.setCurrency(PriceCurrency.fromValue(headerValue));
            } catch (Exception e) {
                log.debug("Invalid currency provided: {}, return default RUB", headerValue);
                currencyProvider.setCurrency(PriceCurrency.RUB);
            }
        }

        filterChain.doFilter(request, response);
    }
}
