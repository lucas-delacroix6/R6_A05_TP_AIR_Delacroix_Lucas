package com.example.tp_air.dto;

import java.util.Collections;
import java.util.List;

public class ApiErrorDTO {
    private final String error;
    private final List<String> messages;

    public ApiErrorDTO(String error, String message) {
        this.error = error;
        this.messages = Collections.singletonList(message);
    }

    public ApiErrorDTO(String error, List<String> messages) {
        this.error = error;
        this.messages = messages;
    }

    public String getError() {
        return error;
    }

    public List<String> getMessages() {
        return messages;
    }
}