package com.jjsttk.goodswarehouse.service.customer.integration.tax;

import java.util.List;
import java.util.Map;

public interface TaxPayerService {
    Map<String, String> getTaxpayerIdentificationNumbersMap(List<String> customerLogins);
}
