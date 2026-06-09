package com.sarvesh.distributedurlshortener.kafka.consumer;

import com.sarvesh.distributedurlshortener.kafka.event.UrlClickedEvent;
import com.sarvesh.distributedurlshortener.shorturl.entity.ShortUrl;
import com.sarvesh.distributedurlshortener.shorturl.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlClickConsumer {

    private final ShortUrlRepository shortUrlRepository;

    @KafkaListener(
            topics = "url-click-topic",
            groupId = "url-shortener-group"
    )
    public void consume(
            UrlClickedEvent event
    ) {

        log.info(
                "Received click event for {}",
                event.getShortCode()
        );

        ShortUrl shortUrl =
                shortUrlRepository
                        .findByShortCode(
                                event.getShortCode()
                        )
                        .orElseThrow();

        shortUrl.setClickCount(
                shortUrl.getClickCount() + 1
        );

        shortUrlRepository.save(
                shortUrl
        );
    }
}