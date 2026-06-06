package com.sarvesh.distributedurlshortener.exception;

public class ShortUrlInactiveException
        extends RuntimeException {

    public ShortUrlInactiveException(
            String message
    ) {
        super(message);
    }
}