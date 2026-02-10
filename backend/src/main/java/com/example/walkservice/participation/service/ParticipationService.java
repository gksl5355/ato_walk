package com.example.walkservice.participation.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.participation.dto.ParticipationResponse;
import com.example.walkservice.participation.entity.Participation;
import com.example.walkservice.participation.entity.ParticipationStatus;
import com.example.walkservice.participation.repository.MeetupLookupRepository;
import com.example.walkservice.participation.repository.ParticipationRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final MeetupLookupRepository meetupLookupRepository;
    private final BlockedWriteGuard blockedWriteGuard;

    public ParticipationService(
            ParticipationRepository participationRepository,
            MeetupLookupRepository meetupLookupRepository,
            BlockedWriteGuard blockedWriteGuard
    ) {
        this.participationRepository = participationRepository;
        this.meetupLookupRepository = meetupLookupRepository;
        this.blockedWriteGuard = blockedWriteGuard;
    }

    public ParticipationResponse requestParticipation(Long actorUserId, Long meetupId) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "PARTICIPATION_REQUEST_FORBIDDEN");

        if (!meetupLookupRepository.existsById(meetupId)) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }

        Participation participation = new Participation(
                meetupId,
                actorUserId,
                ParticipationStatus.REQUESTED,
                OffsetDateTime.now()
        );

        Participation saved = participationRepository.save(participation);
        return toResponse(saved);
    }

    public ParticipationResponse approveParticipation(Long actorUserId, Long meetupId, Long participationId) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "PARTICIPATION_APPROVE_FORBIDDEN");

        Long hostUserId = meetupLookupRepository.findHostUserId(meetupId);
        if (hostUserId == null) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }
        if (!hostUserId.equals(actorUserId)) {
            throw new ApiException("PARTICIPATION_APPROVE_FORBIDDEN", "Only host can approve participation");
        }

        Participation participation = participationRepository.findByIdAndMeetupId(participationId, meetupId)
                .orElseThrow(() -> new ApiException("PARTICIPATION_FIND_NOT_FOUND", "Participation not found"));

        participation.approve();
        return toResponse(participation);
    }

    public ParticipationResponse rejectParticipation(Long actorUserId, Long meetupId, Long participationId) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "PARTICIPATION_REJECT_FORBIDDEN");

        Long hostUserId = meetupLookupRepository.findHostUserId(meetupId);
        if (hostUserId == null) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }
        if (!hostUserId.equals(actorUserId)) {
            throw new ApiException("PARTICIPATION_REJECT_FORBIDDEN", "Only host can reject participation");
        }

        Participation participation = participationRepository.findByIdAndMeetupId(participationId, meetupId)
                .orElseThrow(() -> new ApiException("PARTICIPATION_FIND_NOT_FOUND", "Participation not found"));

        participation.reject();
        return toResponse(participation);
    }

    @Transactional(readOnly = true)
    public Page<ParticipationResponse> listRequestedParticipations(Long actorUserId, Long meetupId, Pageable pageable) {
        Long hostUserId = meetupLookupRepository.findHostUserId(meetupId);
        if (hostUserId == null) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }
        if (!hostUserId.equals(actorUserId)) {
            throw new ApiException("PARTICIPATION_LIST_FORBIDDEN", "Only host can view participation requests");
        }

        return participationRepository
                .findByMeetupIdAndStatusOrderByCreatedAtDesc(meetupId, ParticipationStatus.REQUESTED, pageable)
                .map(this::toResponse);
    }

    private ParticipationResponse toResponse(Participation participation) {
        return new ParticipationResponse(
                participation.getId(),
                participation.getMeetupId(),
                participation.getUserId(),
                participation.getStatus(),
                participation.getCreatedAt()
        );
    }
}
