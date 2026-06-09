package com.sarvesh.distributedurlshortener.shorturl.controller;

import com.sarvesh.distributedurlshortener.exception.RateLimitExceededException;
import com.sarvesh.distributedurlshortener.kafka.event.UrlClickedEvent;
import com.sarvesh.distributedurlshortener.kafka.producer.UrlClickProducer;
import com.sarvesh.distributedurlshortener.ratelimit.RateLimitService;
import com.sarvesh.distributedurlshortener.shorturl.dto.*;
import com.sarvesh.distributedurlshortener.shorturl.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/short-urls")
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final RateLimitService rateLimitService;
    private final UrlClickProducer urlClickProducer;

    @PostMapping
    public CreateShortUrlResponse createShortUrl(
            @RequestBody CreateShortUrlRequest request,
            HttpServletRequest httpServletRequest
    ) {

        String ipAddress =
                httpServletRequest.getRemoteAddr();

        boolean allowed =
                rateLimitService.isAllowed(ipAddress);

        if (!allowed) {

            throw new RateLimitExceededException(
                    "Too many requests. Try again later."
            );
        }

        return shortUrlService.createShortUrl(request);
    }

    @GetMapping("/my-urls")
    public ResponseEntity<List<MyUrlResponse>>
    getMyUrls() {

        return ResponseEntity.ok(
                shortUrlService.getMyUrls()
        );
    }

    @GetMapping("/{shortCode}/analytics")
    public ResponseEntity<UrlAnalyticsResponse>
    getAnalytics(
            @PathVariable String shortCode
    ) {

        return ResponseEntity.ok(
                shortUrlService.getAnalytics(
                        shortCode
                )
        );
    }

    @PatchMapping("/{shortCode}/deactivate")
    public ResponseEntity<String>
    deactivateUrl(
            @PathVariable String shortCode
    ) {

        shortUrlService.deactivateUrl(
                shortCode
        );

        return ResponseEntity.ok(
                "Short URL deactivated"
        );
    }

    @PatchMapping("/{shortCode}/activate")
    public ResponseEntity<String>
    activateUrl(
            @PathVariable String shortCode
    ) {

        shortUrlService.activateUrl(
                shortCode
        );

        return ResponseEntity.ok(
                "Short URL activated"
        );
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<String>
    deleteUrl(
            @PathVariable String shortCode
    ) {

        shortUrlService.deleteUrl(
                shortCode
        );

        return ResponseEntity.ok(
                "Short URL deleted"
        );
    }

    @PatchMapping("/{shortCode}")
    public ResponseEntity<CreateShortUrlResponse>
    updateUrl(
            @PathVariable String shortCode,
            @Valid @RequestBody
            UpdateUrlRequest request
    ) {

        return ResponseEntity.ok(
                shortUrlService.updateUrl(
                        shortCode,
                        request
                )
        );
    }

    @GetMapping("/test-kafka")
    public String testKafka() {

        urlClickProducer.publishClick(
                new UrlClickedEvent(
                        "TEST123"
                )
        );

        return "Event Sent";
    }
}