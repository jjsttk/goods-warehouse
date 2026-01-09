package com.jjsttk.goodswarehouse.service.order.price;

import com.jjsttk.goodswarehouse.service.exchange.GlobalExchangeService;
import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import com.jjsttk.goodswarehouse.service.exchange.util.converter.DefaultPriceConverter;
import com.jjsttk.goodswarehouse.service.exchange.util.converter.PriceConverter;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.shared.enums.order.OrderStatus;
import com.jjsttk.goodswarehouse.testutil.OrderTestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderPriceExchangeServiceImplTest {
    @Mock
    private CurrencyProvider currencyProviderMock;

    @Mock
    private GlobalExchangeService globalExchangeServiceMock;

    @Spy
    private PriceConverter priceConverter = new DefaultPriceConverter();

    @InjectMocks
    private OrderPriceExchangeServiceImpl sut;

    @Test
    void exchangeShouldExchangeDtoPriceCorrectly() {
        var orderEntityStub =
                OrderTestDataFactory.getOrderEntityWithIdByLengthAndStatus(
                        2,
                        OrderStatus.DONE,
                        BigDecimal.TWO,
                        true
                );

        var baseOrderServiceResponseStub =
                OrderTestDataFactory.getBaseOrderServiceResponse(orderEntityStub);

        var eurRateStub = new BigDecimal("100");
        var sessionCurrencyStub = PriceCurrency.EUR;

        var orderAmountBeforeExchange = baseOrderServiceResponseStub.totalPrice();
        var firstItemPriceBeforeExchange = orderEntityStub.getOrderProducts().getFirst().getPrice();
        var secondItemPriceBeforeExchange = orderEntityStub.getOrderProducts().getLast().getPrice();

        var expectedOrderAmount =
                orderAmountBeforeExchange.divide(eurRateStub, 2, RoundingMode.HALF_UP);
        var expectedFirstItemPrice =
                firstItemPriceBeforeExchange.divide(eurRateStub, 2, RoundingMode.HALF_UP);
        var expectedSecondItemPrice =
                secondItemPriceBeforeExchange.divide(eurRateStub, 2, RoundingMode.HALF_UP);

        when(currencyProviderMock.getCurrency())
                .thenReturn(sessionCurrencyStub);
        when(globalExchangeServiceMock.getActualExchangeRateForCurrency(sessionCurrencyStub))
                .thenReturn(eurRateStub);

        var result = sut.exchange(baseOrderServiceResponseStub);

        assertThat(result.currency()).isEqualByComparingTo(sessionCurrencyStub);
        assertThat(result.totalPrice()).isEqualByComparingTo(expectedOrderAmount);
        assertThat(result.products().getFirst().price()).isEqualByComparingTo(expectedFirstItemPrice);
        assertThat(result.products().getLast().price()).isEqualByComparingTo(expectedSecondItemPrice);

    }
}
