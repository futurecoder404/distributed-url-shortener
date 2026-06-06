package com.sarvesh.distributedurlshortener.shorturl.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateShortUrlResponse {

    private String originalUrl;

    private String shortCode;

    private String shortUrl;
}