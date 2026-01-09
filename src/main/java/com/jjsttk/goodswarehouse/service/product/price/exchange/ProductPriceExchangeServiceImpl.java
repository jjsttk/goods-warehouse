package com.jjsttk.goodswarehouse.service.product.price.exchange;

import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import com.jjsttk.goodswarehouse.service.exchange.util.converter.PriceConverter;
import com.jjsttk.goodswarehouse.service.exchange.util.converter.dto.request.PriceConverterRequest;
import com.jjsttk.goodswarehouse.service.exchange.GlobalExchangeService;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service implementation for converting product catalog prices into session-specific currencies.
 * <p>
 * It provides methods to transform both individual product details and paginated product lists
 * by applying current exchange rates retrieved via {@link GlobalExchangeService}.
 */
@Service
@RequiredArgsConstructor
public class ProductPriceExchangeServiceImpl implements ProductPriceExchangeService {
    private final GlobalExchangeService globalExchangeService;
    private final CurrencyProvider currencyProvider;
    private final PriceConverter priceConverter;

    /**
     * Converts a single product's price to the current session currency.
     *
     * @param source the detailed product data in base currency.
     * @return a {@link ProductPriceExchangeServiceResponse} with the converted price.
     */
    public ProductPriceExchangeServiceResponse exchange(ProductServiceProductDetailedResponse source) {
        var sessionCurrency = currencyProvider.getCurrency();
        var rate = globalExchangeService.getActualExchangeRateForCurrency(sessionCurrency);
        var resultPrice = convert(source.price(), rate);

        return buildResponse(source, resultPrice, sessionCurrency);
    }

    /**
     * Converts a paginated list of products to the current session currency.
     * <p>
     * Optimization: Fetches the exchange rate once and applies it to the entire page
     * using {@link Page#map(java.util.function.Function)}.
     *
     * @param source a page of products in base currency.
     * @return a page of products with converted prices and currency.
     */
    public Page<ProductPriceExchangeServiceResponse> exchange(Page<ProductServiceProductDetailedResponse> source) {
        if (!source.hasContent()) {
            return Page.empty();
        }

        var sessionCurrency = currencyProvider.getCurrency();
        var rate = globalExchangeService.getActualExchangeRateForCurrency(sessionCurrency);

        return source.map(it -> {
            var resultPrice = convert(it.price(), rate);

            return buildResponse(it, resultPrice, sessionCurrency);
        });
    }

    /**
     * Internal helper to execute price conversion logic.
     *
     * @param price source price.
     * @param rate  actual rate.
     * @return converted price.
     * @throws ArithmeticException if rate is zero.
     */
    private BigDecimal convert(BigDecimal price, BigDecimal rate) {
        var request = PriceConverterRequest.builder()
                .sourcePrice(price)
                .actualRate(rate)
                .build();
        return priceConverter.convert(request);
    }

    private ProductPriceExchangeServiceResponse buildResponse(
            ProductServiceProductDetailedResponse source,
            BigDecimal resultPrice,
            PriceCurrency currency
    ) {
        return ProductPriceExchangeServiceResponse
                .builder()
                .id(source.id())
                .name(source.name())
                .article(source.article())
                .description(source.description())
                .category(source.category())
                .price(resultPrice)
                .quantity(source.quantity())
                .currency(currency)
                .isAvailable(source.isAvailable())
                .lastQuantityModified(source.lastQuantityModified())
                .createdAt(source.createdAt())
                .build();
    }
}
