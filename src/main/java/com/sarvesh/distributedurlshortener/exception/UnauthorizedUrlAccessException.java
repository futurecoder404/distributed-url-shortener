package com.sarvesh.distributedurlshortener.exception;

public class UnauthorizedUrlAccessException
        extends RuntimeException {

    public UnauthorizedUrlAccessException(
            String message
    ) {
        super(message);
    }
}