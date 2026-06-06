package com.sarvesh.distributedurlshortener.shorturl.service;

import com.sarvesh.distributedurlshortener.auth.entity.User;
import com.sarvesh.distributedurlshortener.auth.repository.UserRepository;
import com.sarvesh.distributedurlshortener.exception.*;
import com.sarvesh.distributedurlshortener.shorturl.dto.CreateShortUrlRequest;
import com.sarvesh.distributedurlshortener.shorturl.dto.CreateShortUrlResponse;
import com.sarvesh.distributedurlshortener.shorturl.dto.MyUrlResponse;
import com.sarvesh.distributedurlshortener.shorturl.entity.ShortUrl;
import com.sarvesh.distributedurlshortener.shorturl.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.sarvesh.distributedurlshortener.shorturl.dto.UrlAnalyticsResponse;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import com.sarvesh.distributedurlshortener.exception.UnauthorizedUrlAccessException;
import com.sarvesh.distributedurlshortener.shorturl.dto.UpdateUrlRequest;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    public CreateShortUrlResponse createShortUrl(
            CreateShortUrlRequest request
    ) {

        String shortCode;

        if (request.getCustomAlias() != null
                && !request.getCustomAlias().isBlank()) {

            if (shortUrlRepository.existsByShortCode(
                    request.getCustomAlias()
            )) {

                throw new CustomAliasAlreadyExistsException(
                        "Custom alias already exists"
                );
            }

            shortCode = request.getCustomAlias();

        } else {

            shortCode = generateShortCode();
        }

        User user = getCurrentUser();

        LocalDateTime expiresAt = null;

        if (request.getExpiryDays() != null) {

            expiresAt = LocalDateTime.now()
                    .plusDays(request.getExpiryDays());
        }

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(request.getUrl())
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .clickCount(0L)
                .isActive(true)
                .user(user)
                .build();
        ShortUrl savedUrl =
                shortUrlRepository.save(shortUrl);

        redisTemplate.opsForValue().set(
                savedUrl.getShortCode(),
                savedUrl.getOriginalUrl()
        );

        return CreateShortUrlResponse.builder()
                .originalUrl(savedUrl.getOriginalUrl())
                .shortCode(savedUrl.getShortCode())
                .shortUrl(
                        "http://localhost:8080/"
                                + savedUrl.getShortCode()
                )
                .build();
    }

    public String getOriginalUrl(String shortCode) {

        ShortUrl shortUrl =
                shortUrlRepository
                        .findByShortCode(shortCode)
                        .orElseThrow(() ->
                                new ShortUrlNotFoundException(
                                        "Short URL not found"
                                ));

        if (!shortUrl.getIsActive()) {
            throw new ShortUrlInactiveException(
                    "Short URL is inactive"
            );
        }

        if (shortUrl.getExpiresAt() != null
                && LocalDateTime.now()
                .isAfter(shortUrl.getExpiresAt())) {

            throw new ShortUrlExpiredException(
                    "Short URL has expired"
            );
        }

        shortUrl.setClickCount(
                shortUrl.getClickCount() + 1
        );
        shortUrlRepository.save(shortUrl);

        String cachedUrl =
                redisTemplate.opsForValue().get(shortCode);

        if (cachedUrl != null) {

            System.out.println("CACHE HIT");

            return cachedUrl;
        }

        System.out.println("CACHE MISS");

        redisTemplate.opsForValue().set(
                shortCode,
                shortUrl.getOriginalUrl()
        );

        return shortUrl.getOriginalUrl();
    }

    private String generateShortCode() {

        String characters =
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        Random random = new Random();

        String shortCode;

        do {

            StringBuilder generatedCode =
                    new StringBuilder();

            for (int i = 0; i < 6; i++) {

                int index =
                        random.nextInt(
                                characters.length()
                        );

                generatedCode.append(
                        characters.charAt(index)
                );
            }

            shortCode =
                    generatedCode.toString();

        } while (
                shortUrlRepository
                        .existsByShortCode(shortCode)
        );

        return shortCode;
    }

    public List<MyUrlResponse> getMyUrls() {

        User user = getCurrentUser();

        return shortUrlRepository
                .findByUser(user)
                .stream()
                .map(shortUrl ->
                        MyUrlResponse.builder()
                                .originalUrl(
                                        shortUrl.getOriginalUrl()
                                )
                                .shortCode(
                                        shortUrl.getShortCode()
                                )
                                .shortUrl(
                                        "http://localhost:8080/"
                                                + shortUrl.getShortCode()
                                )
                                .clickCount(
                                        shortUrl.getClickCount()
                                )
                                .build()
                )
                .toList();
    }

    public UrlAnalyticsResponse getAnalytics(
            String shortCode
    ) {

        ShortUrl shortUrl =
                getOwnedUrl(shortCode);

        return UrlAnalyticsResponse.builder()
                .originalUrl(
                        shortUrl.getOriginalUrl()
                )
                .shortCode(
                        shortUrl.getShortCode()
                )
                .clickCount(
                        shortUrl.getClickCount()
                )
                .createdAt(
                        shortUrl.getCreatedAt()
                )
                .expiresAt(
                        shortUrl.getExpiresAt()
                )
                .expired(
                        shortUrl.getExpiresAt() != null
                                && LocalDateTime.now()
                                .isAfter(
                                        shortUrl.getExpiresAt()
                                )
                )
                .active(
                        shortUrl.getIsActive()
                )
                .build();
    }

    public void deactivateUrl(
            String shortCode
    ) {

        ShortUrl shortUrl =
                getOwnedUrl(shortCode);

        shortUrl.setIsActive(false);

        shortUrlRepository.save(shortUrl);
    }

    public void activateUrl(
            String shortCode
    ) {

        ShortUrl shortUrl =
                getOwnedUrl(shortCode);

        shortUrl.setIsActive(true);

        shortUrlRepository.save(shortUrl);
    }
    public void deleteUrl(
            String shortCode
    ) {

        ShortUrl shortUrl =
                getOwnedUrl(shortCode);

        shortUrlRepository.delete(shortUrl);

        redisTemplate.delete(shortCode);
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }

    private ShortUrl getOwnedUrl(
            String shortCode
    ) {

        User currentUser =
                getCurrentUser();

        ShortUrl shortUrl =
                shortUrlRepository
                        .findByShortCode(shortCode)
                        .orElseThrow(() ->
                                new ShortUrlNotFoundException(
                                        "Short URL not found"
                                ));

        if (!shortUrl.getUser()
                .getId()
                .equals(
                        currentUser.getId()
                )) {

            throw new UnauthorizedUrlAccessException(
                    "You do not own this URL"
            );
        }

        return shortUrl;
    }
    public CreateShortUrlResponse updateUrl(
            String shortCode,
            UpdateUrlRequest request
    ) {

        ShortUrl shortUrl =
                getOwnedUrl(shortCode);

        shortUrl.setOriginalUrl(
                request.getUrl()
        );

        shortUrlRepository.save(shortUrl);

        redisTemplate.opsForValue().set(
                shortCode,
                request.getUrl()
        );

        return CreateShortUrlResponse.builder()
                .originalUrl(
                        shortUrl.getOriginalUrl()
                )
                .shortCode(
                        shortUrl.getShortCode()
                )
                .shortUrl(
                        "http://localhost:8080/"
                                + shortUrl.getShortCode()
                )
                .build();
    }
}