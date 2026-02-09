package com.example.walkservice.communication.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCommunicationRequest {

    @NotBlank
    private String content;

    protected CreateCommunicationRequest() {
    }

    public CreateCommunicationRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
