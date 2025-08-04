package com.example.kafka.handler;

import com.example.kafka.event.UserEvent;
import com.example.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {
    private final EmailService emailService;

    @KafkaListener(topics = "${topic.name}")
    public void handleEvent(@Payload UserEvent event) {
        log.info("Received event: {}", event);

        emailService.sendAdminNotification(event);
    }
}