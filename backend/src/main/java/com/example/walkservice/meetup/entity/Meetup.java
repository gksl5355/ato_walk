package com.example.walkservice.meetup.entity;

import com.example.walkservice.common.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "meetups")
public class Meetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "host_user_id", nullable = false)
    private Long hostUserId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MeetupStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Meetup() {
    }

    public Meetup(
            Long hostUserId,
            String title,
            String description,
            String location,
            Integer maxParticipants,
            OffsetDateTime scheduledAt,
            MeetupStatus status,
            OffsetDateTime createdAt
    ) {
        this.hostUserId = hostUserId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.scheduledAt = scheduledAt;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void cancel() {
        if (this.status != MeetupStatus.RECRUITING) {
            throw new ApiException("MEETUP_CANCEL_INVALID_STATE", "Meetup can only be canceled while recruiting");
        }
        this.status = MeetupStatus.CANCELED;
    }

    public void end() {
        if (this.status != MeetupStatus.RECRUITING) {
            throw new ApiException("MEETUP_END_INVALID_STATE", "Meetup can only be ended while recruiting");
        }
        this.status = MeetupStatus.ENDED;
    }

    public void update(
            String title,
            String description,
            String location,
            Integer maxParticipants,
            OffsetDateTime scheduledAt
    ) {
        if (this.status != MeetupStatus.RECRUITING) {
            throw new ApiException("MEETUP_UPDATE_INVALID_STATE", "Meetup can only be updated while recruiting");
        }
        this.title = title;
        this.description = description;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.scheduledAt = scheduledAt;
    }

    public Long getId() {
        return id;
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public MeetupStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
