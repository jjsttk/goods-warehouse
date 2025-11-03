package com.jjsttk.goodswarehouse.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

@Schema(description = "Product category")
public enum Category {
    @Schema(description = "Electronic devices and accessories")
    ELECTRONICS,

    @Schema(description = "Food and beverages")
    FOOD,

    @Schema(description = "Clothing and fashion items")
    CLOTHING,

    @Schema(description = "Toys and games for kids and adults")
    TOYS,

    @Schema(description = "Books, magazines and other printed materials")
    BOOKS,

    @Schema(description = "Home and office furniture")
    FURNITURE,

    @Schema(description = "Beauty and personal care products")
    BEAUTY,

    @Schema(description = "Sports equipment and accessories")
    SPORTS,

    @Schema(description = "Office supplies and stationery")
    OFFICE,

    @Schema(description = "Products for pets and animals")
    PETS;

    @JsonCreator
    public static Category fromValue(@Nullable String str) {
        return str == null ? null : Category.valueOf(str.strip().toUpperCase());
    }
}
