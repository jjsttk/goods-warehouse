package com.jjsttk.goodswarehouse.shared.configuration.service.exchange;

import com.jjsttk.goodswarehouse.shared.configuration.property.service.exchange.ExchangeServiceProperties;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class ExchangeServiceWebClientFactory {
    private final ExchangeServiceWebClientStrategy strategy;
    private final ExchangeServiceProperties properties;


    public WebClient createWebClient() {
        var httpClient = createHttpClient(properties.getTimeout());
        var filterList = getFilters(properties.getTimeout().getRetry());
        return buildWebClient(httpClient, filterList);
    }

    private HttpClient createHttpClient(ExchangeServiceProperties.TimeoutSettings timeoutSettings) {
        return HttpClient.create()
                .responseTimeout(timeoutSettings.getRead())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) timeoutSettings.getConnect().toMillis());

    }

    private List<ExchangeFilterFunction> getFilters(ExchangeServiceProperties.RetrySettings retrySettings) {
        var readTimeoutFilter = getReadTimeoutFilter(retrySettings.getReadTimeout());
        var internalErrorFilter = getInternalErrorFilter(retrySettings.getServerErrors());
        return List.of(readTimeoutFilter, internalErrorFilter);
    }

    private ExchangeFilterFunction getReadTimeoutFilter(
            ExchangeServiceProperties.RetryPolicy readTimeoutPolicy
    ) {
        return strategy.getReadTimeoutRetryFilterFunction(
                readTimeoutPolicy.getMaxAttempts(), readTimeoutPolicy.getBackoff()
        );
    }

    private ExchangeFilterFunction getInternalErrorFilter(
            ExchangeServiceProperties.RetryPolicy serverErrorPolicy
    ) {
        return strategy.getServerErrorRetryFilterFunction(
                serverErrorPolicy.getMaxAttempts(), serverErrorPolicy.getBackoff()
        );
    }

    private WebClient buildWebClient(HttpClient httpClient, List<ExchangeFilterFunction> filters) {
        var builder = WebClient.builder()
                .baseUrl(properties.getHost())
                .clientConnector(new ReactorClientHttpConnector(httpClient));

        filters.forEach(builder::filter);

        return builder.build();
    }


}
