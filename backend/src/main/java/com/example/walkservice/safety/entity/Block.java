package com.example.walkservice.safety.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "blocks")
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blocker_user_id", nullable = false)
    private Long blockerUserId;

    @Column(name = "blocked_user_id", nullable = false)
    private Long blockedUserId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Block() {
    }

    public Block(Long blockerUserId, Long blockedUserId, OffsetDateTime createdAt) {
        this.blockerUserId = blockerUserId;
        this.blockedUserId = blockedUserId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBlockerUserId() {
        return blockerUserId;
    }

    public Long getBlockedUserId() {
        return blockedUserId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
