package com.sarvesh.distributedurlshortener.exception;

public class ShortUrlNotFoundException
        extends RuntimeException {

    public ShortUrlNotFoundException(String message) {
        super(message);
    }
}