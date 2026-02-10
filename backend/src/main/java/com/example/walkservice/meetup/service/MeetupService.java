package com.example.walkservice.meetup.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.meetup.dto.CreateMeetupRequest;
import com.example.walkservice.meetup.dto.MeetupResponse;
import com.example.walkservice.meetup.dto.UpdateMeetupRequest;
import com.example.walkservice.meetup.entity.Meetup;
import com.example.walkservice.meetup.entity.MeetupStatus;
import com.example.walkservice.meetup.repository.MeetupRepository;
import com.example.walkservice.meetup.repository.UserStatusLookupRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MeetupService {

    private static final String USER_STATUS_BLOCKED = "BLOCKED";

    private final MeetupRepository meetupRepository;
    private final UserStatusLookupRepository userStatusLookupRepository;

    public MeetupService(MeetupRepository meetupRepository, UserStatusLookupRepository userStatusLookupRepository) {
        this.meetupRepository = meetupRepository;
        this.userStatusLookupRepository = userStatusLookupRepository;
    }

    public MeetupResponse createMeetup(Long actorUserId, CreateMeetupRequest request) {
        ensureActorNotBlocked(actorUserId, "MEETUP_CREATE_FORBIDDEN");

        Meetup meetup = new Meetup(
                actorUserId,
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getMaxParticipants(),
                request.getScheduledAt(),
                MeetupStatus.RECRUITING,
                OffsetDateTime.now()
        );

        Meetup saved = meetupRepository.save(meetup);
        return toResponse(saved);
    }

    public void cancelMeetup(Long actorUserId, Long meetupId) {
        ensureActorNotBlocked(actorUserId, "MEETUP_CANCEL_FORBIDDEN");

        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));

        if (!meetup.getHostUserId().equals(actorUserId)) {
            throw new ApiException("MEETUP_CANCEL_FORBIDDEN", "Only host can cancel meetup");
        }

        meetup.cancel();
    }

    public void endMeetup(Long actorUserId, Long meetupId) {
        ensureActorNotBlocked(actorUserId, "MEETUP_END_FORBIDDEN");

        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));

        if (!meetup.getHostUserId().equals(actorUserId)) {
            throw new ApiException("MEETUP_END_FORBIDDEN", "Only host can end meetup");
        }

        meetup.end();
    }

    public MeetupResponse updateMeetup(Long actorUserId, Long meetupId, UpdateMeetupRequest request) {
        ensureActorNotBlocked(actorUserId, "MEETUP_UPDATE_FORBIDDEN");

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
                request.getScheduledAt()
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
            String reactivityLevel
    ) {
        return meetupRepository
                .findAllRecruitingWithHostDogFilters(
                        MeetupStatus.RECRUITING.name(),
                        dogSize,
                        sociabilityLevel,
                        reactivityLevel,
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
                meetup.getStatus()
        );
    }

    private void ensureActorNotBlocked(Long actorUserId, String forbiddenCode) {
        String status = userStatusLookupRepository.findStatusByUserId(actorUserId);
        if (USER_STATUS_BLOCKED.equals(status)) {
            throw new ApiException(forbiddenCode, "Blocked user cannot perform write actions");
        }
    }
}
