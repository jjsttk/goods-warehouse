package com.jjsttk.goodswarehouse.enums;

import io.swagger.v3.oas.annotations.media.Schema;

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
    PETS
}
