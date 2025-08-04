package com.example.kafka.event;

import com.example.kafka.event.enums.Action;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserEvent {
    private String userName;
    private String email;
    private Action action;

    @Override
    public String toString() {
        return String.format("UserEvent[username=%s, email=%s, action=%s]", userName, email, action);
    }
}