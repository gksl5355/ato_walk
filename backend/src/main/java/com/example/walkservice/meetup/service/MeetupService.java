package com.example.walkservice.meetup.service;

import com.example.walkservice.common.exception.ApiException;
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

    public MeetupService(MeetupRepository meetupRepository) {
        this.meetupRepository = meetupRepository;
    }

    public MeetupResponse createMeetup(Long actorUserId, CreateMeetupRequest request) {
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
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));

        if (!meetup.getHostUserId().equals(actorUserId)) {
            throw new ApiException("MEETUP_CANCEL_FORBIDDEN", "Only host can cancel meetup");
        }

        meetup.cancel();
    }

    public void endMeetup(Long actorUserId, Long meetupId) {
        Meetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));

        if (!meetup.getHostUserId().equals(actorUserId)) {
            throw new ApiException("MEETUP_END_FORBIDDEN", "Only host can end meetup");
        }

        meetup.end();
    }

    public MeetupResponse updateMeetup(Long actorUserId, Long meetupId, UpdateMeetupRequest request) {
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
                .findAllRecruitingWithDogFilters(
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
}
