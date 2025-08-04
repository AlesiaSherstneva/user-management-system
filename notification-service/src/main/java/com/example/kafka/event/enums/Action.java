package com.example.kafka.event.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Action {
    CREATED("Создан"),
    UPDATED("Изменён"),
    DELETED("Удалён");

    private final String inRussian;
}