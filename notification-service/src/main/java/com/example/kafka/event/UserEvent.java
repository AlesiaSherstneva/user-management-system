package com.example.kafka.event;

import com.example.kafka.event.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private String userName;
    private String email;
    private Action action;

    @Override
    public String toString() {
        return String.format("UserEvent[username=%s, email=%s, action=%s]", userName, email, action);
    }
}