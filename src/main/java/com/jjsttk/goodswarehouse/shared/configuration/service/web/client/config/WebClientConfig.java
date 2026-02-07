package com.jjsttk.goodswarehouse.shared.configuration.service.web.client.config;

import com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory.AccountServiceWebClientFactory;
import com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory.ExchangeServiceWebClientFactory;
import com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory.InnWebClientFactory;
import com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory.WebClientAbstractFactory;
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
     * Creates a configured WebClient instance for exchange service communication.
     *
     * <p>This bean provides a pre-configured WebClient specifically tailored for
     * communicating with external exchange rate service. The client includes:
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
     * @see WebClientAbstractFactory#create()
     */
    @Bean
    public WebClient exchangeServiceWebClient(ExchangeServiceWebClientFactory factory) {
        return factory.create();
    }

    /**
     * Creates a configured WebClient instance for Inn service communication.
     *
     * <p>This bean provides a pre-configured WebClient specifically tailored for
     * communicating with external Inn web service. The client includes:
     * <ul>
     *   <li>Timeout configurations (connect and read timeouts)</li>
     *   <li>Retry strategies for read timeouts and server errors</li>
     *   <li>Fixed backoff for retry mechanisms</li>
     *   <li>Proper connection pooling and HTTP client settings</li>
     * </ul>
     * </p>
     *
     * @param factory the factory responsible for creating and configuring
     *                the Inn service WebClient instance
     * @return fully configured WebClient instance for Inn service API calls
     * @see WebClientAbstractFactory#create()
     */
    @Bean
    public WebClient innWebClient(InnWebClientFactory factory) {
        return factory.create();
    }

    /**
     * Creates a configured WebClient instance for Account service communication.
     *
     * <p>This bean provides a pre-configured WebClient specifically tailored for
     * communicating with external Account web service. The client includes:
     * <ul>
     *   <li>Timeout configurations (connect and read timeouts)</li>
     *   <li>Retry strategies for read timeouts and server errors</li>
     *   <li>Fixed backoff for retry mechanisms</li>
     *   <li>Proper connection pooling and HTTP client settings</li>
     * </ul>
     * </p>
     *
     * @param factory the factory responsible for creating and configuring
     *                the Account service WebClient instance
     * @return fully configured WebClient instance for Account service API calls
     * @see WebClientAbstractFactory#create()
     */
    @Bean
    public WebClient accountServiceWebClient(AccountServiceWebClientFactory factory) {
        return factory.create();
    }
}
