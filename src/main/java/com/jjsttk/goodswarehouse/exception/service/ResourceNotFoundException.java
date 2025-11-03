package com.jjsttk.goodswarehouse.exception.service;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final UUID resourceId;

    public ResourceNotFoundException(UUID id) {
        super(String.format("Resource with id = %s not found", id));
        this.resourceId = id;
    }
}
