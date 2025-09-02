package com.jjsttk.goodswarehouse.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.NONE)
public final class ExceptionMessage {
    public static String entityNotFoundMessage(Class<?> entityClass, UUID id) {
        return "%s with id %s not found".formatted(entityClass.getSimpleName(), id);
    }

    public static String entityWithThisIdAlreadyUsesThisArticleMessage(Class<?> entityClass, UUID id) {
        return "%s with id = %s, already uses this article".formatted(entityClass.getSimpleName(), id);
    }

    public static String categoryDoesntExists(String value) {
        return "Category '%s' doesnt exists".formatted(value);
    }
}
