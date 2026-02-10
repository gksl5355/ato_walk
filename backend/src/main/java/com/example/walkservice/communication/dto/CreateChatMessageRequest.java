package com.example.walkservice.communication.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateChatMessageRequest {

    @NotBlank
    private String content;

    protected CreateChatMessageRequest() {
    }

    public CreateChatMessageRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
