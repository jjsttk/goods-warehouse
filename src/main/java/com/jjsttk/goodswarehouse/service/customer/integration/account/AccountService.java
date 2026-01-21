package com.jjsttk.goodswarehouse.service.customer.integration.account;

import java.util.List;
import java.util.Map;

public interface AccountService {
    Map<String, String> getAccountNumbersMap(List<String> customerLogins);
}
