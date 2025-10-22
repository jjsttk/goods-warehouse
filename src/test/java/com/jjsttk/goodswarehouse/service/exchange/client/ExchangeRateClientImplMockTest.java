package com.jjsttk.goodswarehouse.service.exchange.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExchangeRateClientImplMockTest {
    private ExchangeRateClientImplMock sut;

    @BeforeEach
    void setUp() {
        sut = new ExchangeRateClientImplMock();
    }

    @Test
    void getExchangeDataShouldReturnExchangeDataWithMapContainsRandomRates() {
        var res = sut.getExchangeData();

        assertThat(res).isNotNull();
        assertThat(res.rates()).isNotEmpty();
        assertThat(res.rates()).hasSize(3);
    }
}
