package com.jjsttk.goodswarehouse.service.product.price.exchange;

import com.jjsttk.goodswarehouse.service.exchange.PriceExchangeService;
import com.jjsttk.goodswarehouse.service.product.dto.response.ProductServiceProductDetailedResponse;
import com.jjsttk.goodswarehouse.service.product.price.exchange.dto.response.ProductPriceExchangeServiceResponse;

public interface ProductPriceExchangeService
        extends PriceExchangeService<ProductServiceProductDetailedResponse, ProductPriceExchangeServiceResponse> {
}
