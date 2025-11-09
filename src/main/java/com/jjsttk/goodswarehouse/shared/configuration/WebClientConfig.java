package com.jjsttk.goodswarehouse.shared.configuration;

import com.jjsttk.goodswarehouse.shared.configuration.service.exchange.ExchangeServiceWebClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for WebClient instances used in the application.
 * Configures timeouts, retry strategies, and connection settings for HTTP clients.
 */
@Slf4j
@Configuration
public class WebClientConfig {

    /**
     * Creates a configured WebClient instance for exchange rate service communication.
     *
     * <p>This bean provides a pre-configured WebClient specifically tailored for
     * communicating with external exchange rate services. The client includes:
     * <ul>
     *   <li>Timeout configurations (connect and read timeouts)</li>
     *   <li>Retry strategies for read timeouts and server errors</li>
     *   <li>Fixed backoff for retry mechanisms</li>
     *   <li>Proper connection pooling and HTTP client settings</li>
     * </ul>
     * </p>
     *
     * @param factory the factory responsible for creating and configuring
     *                the exchange service WebClient instance
     * @return fully configured WebClient instance for exchange service API calls
     * @see ExchangeServiceWebClientFactory#createWebClient()
     */
    @Bean
    public WebClient exchangeServiceWebClient(ExchangeServiceWebClientFactory factory) {
        return factory.createWebClient();
    }
}
