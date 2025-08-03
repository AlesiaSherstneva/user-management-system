package com.example.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic userEventsTopic() {
        NewTopic userEventsTopic = TopicBuilder.name("user-events")
                .partitions(1)
                .replicas(1)
                .build();

        log.info("Topic created: {}", userEventsTopic.name());

        return userEventsTopic;
    }
}