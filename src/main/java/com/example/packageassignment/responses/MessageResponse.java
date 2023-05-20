package com.example.packageassignment.responses;

import com.example.packageassignment.model.Driver;

import java.util.Optional;

public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(Optional<Driver> employee) {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
