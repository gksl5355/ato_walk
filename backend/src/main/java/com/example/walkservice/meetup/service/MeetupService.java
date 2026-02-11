package com.example.walkservice.meetup.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.meetup.dto.CreateMeetupRequest;
import com.example.walkservice.meetup.dto.MeetupResponse;
import com.example.walkservice.meetup.dto.UpdateMeetupRequest;
import com.example.walkservice.meetup.entity.Meetup;
import com.example.walkservice.meetup.entity.MeetupStatus;
import com.example.walkservice.meetup.repository.MeetupRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeetupService {

    private final MeetupRepository meetupRepository;
    private final BlockedWriteGuard blockedWriteGuard;

    public MeetupService(MeetupRepository meetupRepository, BlockedWriteGuard blockedWriteGuard) {
        this.meetupRepository = meetupRepository;
        this.blockedWriteGuard = blockedWriteGuard;
    }

    public MeetupResponse createMeetup(Long actorUserId, CreateMeetupRequest request) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "MEETUP_CREATE_FORBIDDEN");

        Meetup meetup = new Meetup(
                actorUserId,
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getMaxParticipants(),
                request.getScheduledAt(),
                request.getDogSize(),
                request.getSociabilityLevel(),
                request.getReactivityLevel(),
                request.getNeutered(),
                MeetupStatus.RECRUITING,
                OffsetDateTime.now()
        );

        Meetup saved = meetupRepository.save(meetup);
        return toResponse(saved);
    }

    public void cancelMeetup(Long actorUserId, Long meetupId) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "MEETUP_CANCEL_FORBIDDEN");

        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));

        if (!meetup.getHostUserId().equals(actorUserId)) {
            throw new ApiException("MEETUP_CANCEL_FORBIDDEN", "Only host can cancel meetup");
        }

        meetup.cancel();
    }

    public void endMeetup(Long actorUserId, Long meetupId) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "MEETUP_END_FORBIDDEN");

        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));

        if (!meetup.getHostUserId().equals(actorUserId)) {
            throw new ApiException("MEETUP_END_FORBIDDEN", "Only host can end meetup");
        }

        meetup.end();
    }

    public MeetupResponse updateMeetup(Long actorUserId, Long meetupId, UpdateMeetupRequest request) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "MEETUP_UPDATE_FORBIDDEN");

        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));

        if (!meetup.getHostUserId().equals(actorUserId)) {
            throw new ApiException("MEETUP_UPDATE_FORBIDDEN", "Only host can update meetup");
        }

        meetup.update(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getMaxParticipants(),
                request.getScheduledAt(),
                request.getDogSize(),
                request.getSociabilityLevel(),
                request.getReactivityLevel(),
                request.getNeutered()
        );

        return toResponse(meetup);
    }

    @Transactional(readOnly = true)
    public MeetupResponse getMeetup(Long meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));
        return toResponse(meetup);
    }

    @Transactional(readOnly = true)
    public Page<MeetupResponse> listRecruitingMeetups(
            Pageable pageable,
            String dogSize,
            String sociabilityLevel,
            String reactivityLevel,
            Boolean neutered
    ) {
        return meetupRepository
                .findAllRecruitingWithMeetupFilters(
                        MeetupStatus.RECRUITING.name(),
                        dogSize,
                        sociabilityLevel,
                        reactivityLevel,
                        neutered,
                        pageable
                )
                .map(this::toResponse);
    }

    private MeetupResponse toResponse(Meetup meetup) {
        return new MeetupResponse(
                meetup.getId(),
                meetup.getHostUserId(),
                meetup.getTitle(),
                meetup.getDescription(),
                meetup.getLocation(),
                meetup.getMaxParticipants(),
                meetup.getScheduledAt(),
                meetup.getDogSize(),
                meetup.getSociabilityLevel(),
                meetup.getReactivityLevel(),
                meetup.getNeutered(),
                meetup.getStatus()
        );
    }

}
