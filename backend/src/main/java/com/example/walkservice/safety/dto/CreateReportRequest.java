package com.example.walkservice.safety.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateReportRequest {

    @NotNull
    private final Long reportedUserId;

    private final Long meetupId;

    @NotBlank
    private final String reason;

    public CreateReportRequest(Long reportedUserId, Long meetupId, String reason) {
        this.reportedUserId = reportedUserId;
        this.meetupId = meetupId;
        this.reason = reason;
    }

    public Long getReportedUserId() {
        return reportedUserId;
    }

    public Long getMeetupId() {
        return meetupId;
    }

    public String getReason() {
        return reason;
    }
}
