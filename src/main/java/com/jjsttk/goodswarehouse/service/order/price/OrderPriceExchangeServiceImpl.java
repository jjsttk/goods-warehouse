package com.jjsttk.goodswarehouse.service.order.price;

import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import com.jjsttk.goodswarehouse.service.order.dto.response.BaseOrderServiceResponse;
import com.jjsttk.goodswarehouse.service.order.dto.response.OrderServiceProductInOrderResponse;
import com.jjsttk.goodswarehouse.service.exchange.util.converter.PriceConverter;
import com.jjsttk.goodswarehouse.service.exchange.util.converter.dto.request.PriceConverterRequest;
import com.jjsttk.goodswarehouse.service.exchange.GlobalExchangeService;
import com.jjsttk.goodswarehouse.service.order.price.dto.response.OrderPriceExchangeServiceResponse;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service implementation responsible for recalculating order prices into the current session's currency.
 * <p>
 * This service coordinates the retrieval of the target currency, fetches the latest exchange rates.
 * Performs mass conversion of individual order items and the total order amount.
 */
@Service
@RequiredArgsConstructor
public class OrderPriceExchangeServiceImpl implements OrderPriceExchangeService {
    private final CurrencyProvider currencyProvider;
    private final GlobalExchangeService globalExchangeService;
    private final PriceConverter priceConverter;

    /**
     * Converts the entire order data from the base currency to the user's active session currency.
     *
     * @param baseOrderServiceResponse the source order data containing prices in base currency.
     * @return a new {@link OrderPriceExchangeServiceResponse} with all prices converted and the target currency specified.
     * @throws RuntimeException if exchange rate retrieval or conversion fails.
     */
    @Override
    public OrderPriceExchangeServiceResponse exchange(BaseOrderServiceResponse baseOrderServiceResponse) {
        var sessionCurrency = currencyProvider.getCurrency();
        var actualRate = globalExchangeService.getActualExchangeRateForCurrency(sessionCurrency);

        var totalPriceConverted = convertPrice(baseOrderServiceResponse.totalPrice(), actualRate);

        var priceExchangedProducts = convertPriceOfOrderedProducts(
                baseOrderServiceResponse.products(),
                actualRate
        );

        return buildResponse(
                baseOrderServiceResponse,
                priceExchangedProducts,
                sessionCurrency,
                totalPriceConverted
        );
    }

    /**
     * Iterates through the list of products and converts the price for each individual item.
     *
     * @param source     the list of original products.
     * @param actualRate the exchange rate to apply.
     * @return a list of products with updated prices.
     */
    private List<OrderServiceProductInOrderResponse> convertPriceOfOrderedProducts(
            List<OrderServiceProductInOrderResponse> source,
            BigDecimal actualRate
    ) {
        return source.stream()
                .map(it -> buildProductInOrderResponse(
                        it,
                        convertPrice(it.price(), actualRate)
                ))
                .toList();
    }

    /**
     * Helper method to invoke the {@link PriceConverter} for a single value.
     *
     * @param price      source price before convert to new value by actual rate.
     * @param actualRate the exchange rate to apply.
     * @return converted price for actual rate.
     */
    private BigDecimal convertPrice(BigDecimal price, BigDecimal actualRate) {
        var request = PriceConverterRequest.builder()
                .sourcePrice(price)
                .actualRate(actualRate)
                .build();

        return priceConverter.convert(request);
    }

    private OrderPriceExchangeServiceResponse buildResponse(
            BaseOrderServiceResponse orderServiceResponseSource,
            List<OrderServiceProductInOrderResponse> priceExchangedProducts,
            PriceCurrency sessionCurrency,
            BigDecimal totalPriceConverted
    ) {
        return OrderPriceExchangeServiceResponse.builder()
                .orderId(orderServiceResponseSource.orderId())
                .products(priceExchangedProducts)
                .currency(sessionCurrency)
                .totalPrice(totalPriceConverted)
                .build();
    }

    private OrderServiceProductInOrderResponse buildProductInOrderResponse(
            OrderServiceProductInOrderResponse source,
            BigDecimal convertedProductPrice
    ) {
        return OrderServiceProductInOrderResponse.builder()
                .productId(source.productId())
                .name(source.name())
                .price(convertedProductPrice)
                .quantity(source.quantity())
                .build();
    }
}
