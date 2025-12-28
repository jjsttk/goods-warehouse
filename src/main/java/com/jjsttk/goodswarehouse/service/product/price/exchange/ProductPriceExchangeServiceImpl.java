package com.jjsttk.goodswarehouse.service.product.price.exchange;

import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import com.jjsttk.goodswarehouse.shared.enums.exchange.PriceCurrency;
import com.jjsttk.goodswarehouse.exception.service.exchange.deserializer.ExchangeRateNotFoundException;
import com.jjsttk.goodswarehouse.service.exchange.currency.provider.CurrencyProvider;
import com.jjsttk.goodswarehouse.service.exchange.provider.ExchangeRateProvider;
import com.jjsttk.goodswarehouse.service.exchange.dto.request.ExchangeData;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Service for converting product prices to the currently selected currency.
 * <p>
 * This implementation uses {@link CurrencyProvider} to determine the target currency
 * for the current session. If the target currency is {@link PriceCurrency#RUB},
 * no conversion is performed and the original price is returned.
 * <p>
 * For other currencies, the service fetches exchange rates from the configured
 * {@link ExchangeRateProvider}. The conversion uses {@link BigDecimal} for
 * precise decimal arithmetic with rounding mode set to {@link RoundingMode#HALF_UP}.
 * </p>
 * <p>
 * Conversion results are returned as {@link ProductPriceExchangeServiceResponse}. Methods
 * support both single price conversion and batch conversion for a paginated list of prices.
 * </p>
 *
 * @see CurrencyProvider
 * @see ExchangeRateProvider
 * @see ProductPriceExchangeServiceResponse
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductPriceExchangeServiceImpl
        implements ProductPriceExchangeService {

    private final CurrencyProvider currencyProvider;
    private final ExchangeRateProvider exchangeRateProvider;

    /**
     * Converts a single product's price to the target currency.
     *
     * @param baseServiceDto the product data transfer object containing the original price
     * @return a response object containing the product data with converted price and currency
     */
    @Override
    public ProductPriceExchangeServiceResponse exchange(ProductServiceProductDetailedResponse baseServiceDto) {
        PriceCurrency currency = currencyProvider.getCurrency();
        BigDecimal rate = getExchangeRateForCurrency(currency);

        return convertProductPrice(baseServiceDto, currency, rate);
    }

    /**
     * Converts a page of product prices to the target currency.
     * <p>
     * Uses a single exchange rate lookup for the entire page to optimize performance.
     * Returns an empty page if the input page has no content.
     * </p>
     *
     * @param baseProductServiceDtoPage a page of product data transfer objects
     * @return a page of response objects with converted prices and currency
     */
    @Override
    public Page<ProductPriceExchangeServiceResponse> exchange(
            Page<ProductServiceProductDetailedResponse> baseProductServiceDtoPage
    ) {
        if (!baseProductServiceDtoPage.hasContent()) {
            return Page.empty();
        }

        PriceCurrency currency = currencyProvider.getCurrency();
        BigDecimal rate = getExchangeRateForCurrency(currency);

        return baseProductServiceDtoPage.map(dto -> convertProductPrice(dto, currency, rate));
    }

    /**
     * Gets the exchange rate for the specified currency.
     * <p>
     * For {@link PriceCurrency#RUB} returns 1.0. For other currencies, fetches
     * the current rate from the {@link ExchangeRateProvider}. If the rate is not found,
     * logs an error and falls back to RUB with rate 1.0.
     * </p>
     *
     * @param currency the target currency
     * @return the exchange rate for conversion, always returns {@link BigDecimal#ONE} for RUB
     * @throws ExchangeRateNotFoundException if the rate for the specified currency is not available
     */
    private BigDecimal getExchangeRateForCurrency(PriceCurrency currency) {
        if (currency == PriceCurrency.RUB) {
            return BigDecimal.ONE;
        }

        try {
            ExchangeData exchangeData = exchangeRateProvider.getExchangeData();
            return Optional.ofNullable(exchangeData.rates().get(currency))
                    .orElseThrow(() -> new ExchangeRateNotFoundException(currency));

        } catch (ExchangeRateNotFoundException e) {
            return BigDecimal.ONE;
        }
    }

    /**
     * Converts product price using the provided exchange rate.
     *
     * @param dto      product data
     * @param currency target currency
     * @param rate     exchange rate for conversion
     * @return response with converted price
     */
    private ProductPriceExchangeServiceResponse convertProductPrice(
            ProductServiceProductDetailedResponse dto, PriceCurrency currency, BigDecimal rate) {

        BigDecimal convertedPrice = convertPrice(dto.price(), rate);
        return createResponse(dto, convertedPrice, currency);
    }

    /**
     * Creates a response DTO from product data, price, and currency.
     *
     * @param dto      the original product data
     * @param price    the converted price
     * @param currency the target currency
     * @return the assembled response DTO
     */
    private ProductPriceExchangeServiceResponse createResponse(
            ProductServiceProductDetailedResponse dto, BigDecimal price, PriceCurrency currency) {
        return ProductPriceExchangeServiceResponse.from(dto, price, currency);
    }

    /**
     * Converts a price using an exchange rate with precise decimal arithmetic.
     * <p>
     * Uses {@link BigDecimal#divide(BigDecimal, int, RoundingMode)} to ensure
     * exact calculation with scale set to 2 decimal places and
     * {@link RoundingMode#HALF_UP} rounding mode.
     * </p>
     *
     * @param price the original price to convert
     * @param rate  the exchange rate to apply
     * @return the converted price with 2 decimal places
     * @throws ArithmeticException if the result cannot be represented exactly
     *                             with the specified scale and rounding mode
     * @see BigDecimal
     * @see RoundingMode#HALF_UP
     */
    private BigDecimal convertPrice(BigDecimal price, BigDecimal rate) {
        return price.divide(rate, 2, RoundingMode.HALF_UP);
    }
}
