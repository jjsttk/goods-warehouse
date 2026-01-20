package com.jjsttk.goodswarehouse.shared.configuration.service.web.client.factory;

import com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient.RetryPolicy;
import com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient.RetrySettings;
import com.jjsttk.goodswarehouse.shared.configuration.property.common.webclient.TimeoutSettings;
import com.jjsttk.goodswarehouse.shared.configuration.property.rest.RestServiceProperties;
import com.jjsttk.goodswarehouse.shared.util.webclient.WebClientRetryUtils;
import io.netty.channel.ChannelOption;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Component
public class WebClientFactoryImpl
        implements WebClientFactory {

    /**
     * {@inheritDoc}
     * <p>
     * This implementation sets up timeouts from {@link TimeoutSettings},
     * applies retry filters, and configures the base URL.
     */
    @Override
    public WebClient create(RestServiceProperties properties) {
        var timeout = properties.getTimeout();
        var httpClient = createHttpClient(timeout);

        var retry = properties.getRetry();
        var filterList = getFilters(retry);

        return buildWebClient(httpClient, properties.getHost(), filterList);
    }

    private HttpClient createHttpClient(TimeoutSettings timeoutSettings) {
        return HttpClient.create()
                .responseTimeout(timeoutSettings.getRead())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutSettings.getConnect().toMillis());
    }

    private List<ExchangeFilterFunction> getFilters(RetrySettings retrySettings) {
        var readTimeoutFilter = getReadTimeoutFilter(retrySettings.getReadTimeout());
        var internalErrorFilter = getInternalErrorFilter(retrySettings.getServerErrors());
        return List.of(readTimeoutFilter, internalErrorFilter);
    }

    private ExchangeFilterFunction getReadTimeoutFilter(
            RetryPolicy readTimeoutPolicy
    ) {
        return WebClientRetryUtils.getReadTimeoutRetryFilterFunction(
                readTimeoutPolicy.getMaxAttempts(), readTimeoutPolicy.getBackoff()
        );
    }

    private ExchangeFilterFunction getInternalErrorFilter(
            RetryPolicy serverErrorPolicy
    ) {
        return WebClientRetryUtils.getServerErrorRetryFilterFunction(
                serverErrorPolicy.getMaxAttempts(), serverErrorPolicy.getBackoff()
        );
    }

    private WebClient buildWebClient(HttpClient httpClient, String host, List<ExchangeFilterFunction> filters) {
        var builder = WebClient.builder()
                .baseUrl(host)
                .clientConnector(new ReactorClientHttpConnector(httpClient));

        filters.forEach(builder::filter);

        return builder.build();
    }
}
