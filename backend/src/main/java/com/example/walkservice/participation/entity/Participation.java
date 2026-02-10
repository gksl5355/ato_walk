package com.example.walkservice.participation.entity;

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
@Table(name = "participations")
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meetup_id", nullable = false)
    private Long meetupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ParticipationStatus status;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Participation() {
    }

    public Participation(Long meetupId, Long userId, ParticipationStatus status, OffsetDateTime createdAt) {
        this.meetupId = meetupId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void approve() {
        if (this.status != ParticipationStatus.REQUESTED) {
            throw new ApiException(
                    "PARTICIPATION_APPROVE_INVALID_STATE",
                    "Participation can only be approved when requested"
            );
        }
        this.status = ParticipationStatus.APPROVED;
    }

    public void reject() {
        if (this.status != ParticipationStatus.REQUESTED) {
            throw new ApiException(
                    "PARTICIPATION_REJECT_INVALID_STATE",
                    "Participation can only be rejected when requested"
            );
        }
        this.status = ParticipationStatus.REJECTED;
    }

    public Long getId() {
        return id;
    }

    public Long getMeetupId() {
        return meetupId;
    }

    public Long getUserId() {
        return userId;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
