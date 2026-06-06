package com.sarvesh.distributedurlshortener.shorturl.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateShortUrlRequest {

    @NotBlank(message = "URL cannot be empty")
    @Pattern(
            regexp = "^(http://|https://).+",
            message = "URL must start with http:// or https://"
    )
    private String url;

    private Integer expiryDays;

    @Pattern(
            regexp = "^[a-zA-Z0-9_-]*$",
            message = "Alias can contain only letters, numbers, - and _"
    )
    private String customAlias;
}