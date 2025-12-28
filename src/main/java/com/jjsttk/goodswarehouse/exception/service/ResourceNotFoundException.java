package com.jjsttk.goodswarehouse.exception.service;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Resource %s with id = %s not found";

    public ResourceNotFoundException(Class<?> resourceClass, UUID id) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, resourceClass.getSimpleName(), id));
    }

    public ResourceNotFoundException(Class<?> resourceClass, Long id) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, resourceClass.getSimpleName(), id));
    }


}
