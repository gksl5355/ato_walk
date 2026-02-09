package com.example.walkservice.meetup.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.meetup.dto.CreateExampleMeetupRequest;
import com.example.walkservice.meetup.dto.ExampleMeetupResponse;
import com.example.walkservice.meetup.entity.ExampleMeetup;
import com.example.walkservice.meetup.entity.ExampleMeetupStatus;
import com.example.walkservice.meetup.repository.ExampleMeetupRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExampleMeetupService {

    private final ExampleMeetupRepository meetupRepository;

    public ExampleMeetupService(ExampleMeetupRepository meetupRepository) {
        this.meetupRepository = meetupRepository;
    }

    public ExampleMeetupResponse createMeetup(Long actorUserId, CreateExampleMeetupRequest request) {
        ExampleMeetup meetup = new ExampleMeetup(
                actorUserId,
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getMaxParticipants(),
                request.getScheduledAt(),
                ExampleMeetupStatus.RECRUITING,
                OffsetDateTime.now()
        );

        ExampleMeetup saved = meetupRepository.save(meetup);
        return toResponse(saved);
    }

    public void cancelMeetup(Long actorUserId, Long meetupId) {
        ExampleMeetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));

        if (!meetup.getHostUserId().equals(actorUserId)) {
            throw new ApiException("MEETUP_CANCEL_FORBIDDEN", "Only host can cancel meetup");
        }

        if (meetup.getStatus() != ExampleMeetupStatus.RECRUITING) {
            throw new ApiException("MEETUP_CANCEL_INVALID_STATE", "Meetup can only be canceled while recruiting");
        }

        meetup.cancel();
    }

    @Transactional(readOnly = true)
    public ExampleMeetupResponse getMeetup(Long meetupId) {
        ExampleMeetup meetup = meetupRepository.findById(meetupId)
                .orElseThrow(() -> new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found"));
        return toResponse(meetup);
    }

    @Transactional(readOnly = true)
    public Page<ExampleMeetupResponse> listRecruitingMeetups(Pageable pageable) {
        return meetupRepository.findAllByStatus(ExampleMeetupStatus.RECRUITING, pageable).map(this::toResponse);
    }

    private ExampleMeetupResponse toResponse(ExampleMeetup meetup) {
        return new ExampleMeetupResponse(
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
