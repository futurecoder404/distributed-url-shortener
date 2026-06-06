package com.sarvesh.distributedurlshortener.exception;

public class ShortUrlExpiredException
        extends RuntimeException {

    public ShortUrlExpiredException(
            String message
    ) {
        super(message);
    }
}