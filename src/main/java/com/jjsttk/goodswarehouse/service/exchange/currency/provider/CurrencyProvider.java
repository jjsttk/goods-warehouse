package com.jjsttk.goodswarehouse.service.exchange.currency.provider;

import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Session-scoped provider for storing the currently selected currency for a user session.
 * <p>
 * This bean holds the {@link PriceCurrency} that will be used throughout the application
 * for price conversions during the lifetime of an HTTP session.
 * <p>
 * Default currency is set to {@link PriceCurrency#RUB} on session creation.
 * <p>
 * The bean is declared with {@link WebApplicationContext#SCOPE_SESSION} and
 * uses a scoped proxy to allow injection into singleton-scoped components like filters or services.
 */
@Component
@SessionScope
@Getter
@Setter
public class CurrencyProvider {

    /**
     * The current currency for the session. Can be updated by a filter or controller
     * based on request headers or user preferences.
     */
    private PriceCurrency currency;

    /**
     * Initializes the session currency to the default value (RUB) after the bean is created.
     */
    @PostConstruct
    public void init() {
        this.currency = PriceCurrency.RUB;
    }
}
