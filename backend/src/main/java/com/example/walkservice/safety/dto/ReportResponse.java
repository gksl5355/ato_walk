package com.example.walkservice.safety.dto;

import java.time.OffsetDateTime;

public class ReportResponse {

    private final Long id;
    private final Long reporterUserId;
    private final Long reportedUserId;
    private final Long meetupId;
    private final String reason;
    private final OffsetDateTime createdAt;

    public ReportResponse(
            Long id,
            Long reporterUserId,
            Long reportedUserId,
            Long meetupId,
            String reason,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.reporterUserId = reporterUserId;
        this.reportedUserId = reportedUserId;
        this.meetupId = meetupId;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getReporterUserId() {
        return reporterUserId;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
