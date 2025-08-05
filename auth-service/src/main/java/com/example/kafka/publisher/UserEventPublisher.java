package com.example.kafka.publisher;

import com.example.kafka.event.UserEvent;
import com.example.kafka.event.enums.Action;
import com.example.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void publishEvent(User user, Action action) {
        try {
            UserEvent event = UserEvent.builder()
                    .userName(String.format("%s %s", user.getFirstName(), user.getLastName()))
                    .email(user.getEmail())
                    .action(action)
                    .build();
            kafkaTemplate.send("user-events", event);

            log.debug("Sent Kafka event: {}", event);
        } catch (Exception ex) {
            log.error("Failed to send Kafka event: {}", ex.getMessage());
        }
    }
}