package com.sarvesh.distributedurlshortener.shorturl.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyUrlResponse {

    private String originalUrl;

    private String shortCode;

    private String shortUrl;

    private Long clickCount;
}