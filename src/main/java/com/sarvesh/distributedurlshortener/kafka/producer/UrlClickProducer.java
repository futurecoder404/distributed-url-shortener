package com.sarvesh.distributedurlshortener.kafka.producer;

import com.sarvesh.distributedurlshortener.kafka.event.UrlClickedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlClickProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC =
            "url-click-topic";

    public void publishClick(
            UrlClickedEvent event
    ) {

        kafkaTemplate.send(
                TOPIC,
                event
        );
    }
}