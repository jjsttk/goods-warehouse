package com.jjsttk.goodswarehouse.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ValidationMessage {

    public static String entityWithThisArticleExistsMessage(Class<?> entityClass, Long article) {
        return "%s with article %d already exists".formatted(entityClass.getSimpleName(), article);
    }

    public static String categoryDoesNotExistsMessage(Class<?> categoryClass, String categoryStr) {
        return "%s with name %s does not exists".formatted(categoryClass.getSimpleName(), categoryStr);
    }

    public static String nameCannotBeBlankMessage() {
        return "Name cannot be blank";
    }

    public static String descriptionCannotBeBlankMessage() {
        return "Description cannot be blank";
    }

    public static String categoryCannotBeBlankMessage() {
        return "Category cannot be blank";
    }

    public static String articleIsNotValidMessage() {
        return "Article must be >= 0";
    }

    public static String notUniqueArticleMessage() {
        return "Article must be unique";
    }

    public static String priceIsNotValidMessage() {
        return "Price must be greater than 0";
    }

    public static String quantityIsNotValidMessage() {
        return "Quantity must be >= 0";
    }
}
