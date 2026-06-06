package com.sarvesh.distributedurlshortener.shorturl.repository;

import com.sarvesh.distributedurlshortener.shorturl.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sarvesh.distributedurlshortener.auth.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShortUrlRepository
        extends JpaRepository<ShortUrl, UUID> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    List<ShortUrl> findByUser(User user);
    void deleteByShortCode(String shortCode);
}