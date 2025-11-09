package com.jjsttk.goodswarehouse.exception.service;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private static final String EXCEPTION_MESSAGE_FORMAT = "Resource with id = %s not found";
    private final UUID resourceId;

    public ResourceNotFoundException(UUID id) {
        super(String.format(EXCEPTION_MESSAGE_FORMAT, id));
        this.resourceId = id;
    }
}
