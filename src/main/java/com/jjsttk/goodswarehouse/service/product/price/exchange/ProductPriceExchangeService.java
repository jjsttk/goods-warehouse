package com.jjsttk.goodswarehouse.service.product.price.exchange;

import com.jjsttk.goodswarehouse.service.exchange.PriceExchangeService;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ExchangeProductPriceResponse;

public interface ProductPriceExchangeService
        extends PriceExchangeService<ProductDetailedResponse, ExchangeProductPriceResponse> {
}
