package com.example.walkservice.participation.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.participation.dto.ParticipationResponse;
import com.example.walkservice.participation.entity.Participation;
import com.example.walkservice.participation.entity.ParticipationStatus;
import com.example.walkservice.participation.repository.DogProfileLookupRepository;
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
    private final DogProfileLookupRepository dogProfileLookupRepository;
    private final BlockedWriteGuard blockedWriteGuard;

    public ParticipationService(
            ParticipationRepository participationRepository,
            MeetupLookupRepository meetupLookupRepository,
            DogProfileLookupRepository dogProfileLookupRepository,
            BlockedWriteGuard blockedWriteGuard
    ) {
        this.participationRepository = participationRepository;
        this.meetupLookupRepository = meetupLookupRepository;
        this.dogProfileLookupRepository = dogProfileLookupRepository;
        this.blockedWriteGuard = blockedWriteGuard;
    }

    public ParticipationResponse requestParticipation(Long actorUserId, Long meetupId) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "PARTICIPATION_REQUEST_FORBIDDEN");

        Long hostUserId = meetupLookupRepository.findHostUserId(meetupId);
        if (hostUserId == null) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }
        if (hostUserId.equals(actorUserId)) {
            throw new ApiException("PARTICIPATION_REQUEST_FORBIDDEN", "Host cannot request own meetup");
        }

        if (participationRepository.existsByMeetupIdAndUserIdAndStatus(meetupId, actorUserId, ParticipationStatus.REQUESTED)) {
            throw new ApiException("PARTICIPATION_REQUEST_DUPLICATE", "Participation request already pending");
        }
        if (participationRepository.existsByMeetupIdAndUserIdAndStatus(meetupId, actorUserId, ParticipationStatus.APPROVED)) {
            throw new ApiException("PARTICIPATION_REQUEST_DUPLICATE", "You are already approved for this meetup");
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

    @Transactional(readOnly = true)
    public ParticipationResponse getMyParticipation(Long actorUserId, Long meetupId) {
        if (!meetupLookupRepository.existsById(meetupId)) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }

        return participationRepository.findTopByMeetupIdAndUserIdOrderByCreatedAtDesc(meetupId, actorUserId)
                .map(this::toResponse)
                .orElse(null);
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

    @Transactional(readOnly = true)
    public Page<ParticipationResponse> listApprovedParticipations(Long meetupId, Pageable pageable) {
        if (!meetupLookupRepository.existsById(meetupId)) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }

        return participationRepository
                .findByMeetupIdAndStatusOrderByCreatedAtAsc(meetupId, ParticipationStatus.APPROVED, pageable)
                .map(this::toResponse);
    }

    private ParticipationResponse toResponse(Participation participation) {
        String nickname = dogProfileLookupRepository.findPrimaryDogNameByUserId(participation.getUserId());
        if (nickname == null || nickname.isBlank()) {
            nickname = "User #" + participation.getUserId();
        }
        return new ParticipationResponse(
                participation.getId(),
                participation.getMeetupId(),
                participation.getUserId(),
                nickname,
                participation.getStatus(),
                participation.getCreatedAt()
        );
    }
}
