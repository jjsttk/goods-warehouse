package com.jjsttk.goodswarehouse.dto.request;

import java.math.BigDecimal;

public interface NormalizableDto {
    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getCategory();
    void setCategory(String category);

    BigDecimal getPrice();
    void setPrice(BigDecimal price);

    Long getArticle();
    void setArticle(Long article);

    Integer getQuantity();
    void setQuantity(Integer quantity);
}
