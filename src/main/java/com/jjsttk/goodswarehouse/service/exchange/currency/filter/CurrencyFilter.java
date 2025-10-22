package com.jjsttk.goodswarehouse.service.exchange.currency.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jjsttk.goodswarehouse.enums.PriceCurrency;
import com.jjsttk.goodswarehouse.exception.response.ErrorResponse;
import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;

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
@Component
@RequiredArgsConstructor
public class CurrencyFilter extends OncePerRequestFilter {

    private final CurrencyProvider currencyProvider;
    private final ObjectMapper objectMapper;

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

        String headerValue = request.getHeader("currency");

        if (headerValue != null && !headerValue.isBlank()) {
            try {
                var normalized = headerValue.strip().toUpperCase();
                currencyProvider.setCurrency(
                        PriceCurrency.valueOf(normalized)
                );
            } catch (IllegalArgumentException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");

                var errorResponse = ErrorResponse.builder()
                        .message(String.format("Unknown currency: %s", headerValue))
                        .dateTime(OffsetDateTime.now())
                        .exception("UnknownCurrencyException")
                        .source("CurrencyFilter")
                        .build();

                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                response.getWriter().flush();
                return;
            }

        }
        filterChain.doFilter(request, response);
    }
}
