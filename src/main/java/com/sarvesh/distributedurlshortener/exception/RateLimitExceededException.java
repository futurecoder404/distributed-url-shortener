package com.sarvesh.distributedurlshortener.exception;

public class RateLimitExceededException
        extends RuntimeException {

    public RateLimitExceededException(
            String message
    ) {
        super(message);
    }
}