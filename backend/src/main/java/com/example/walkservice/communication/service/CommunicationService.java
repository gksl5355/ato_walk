package com.example.walkservice.communication.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.communication.dto.CommunicationResponse;
import com.example.walkservice.communication.dto.CreateCommunicationRequest;
import com.example.walkservice.communication.entity.Communication;
import com.example.walkservice.communication.repository.CommunicationRepository;
import com.example.walkservice.communication.repository.MeetupLookupRepository;
import com.example.walkservice.communication.repository.UserStatusLookupRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommunicationService {

    private static final String USER_STATUS_BLOCKED = "BLOCKED";

    private final CommunicationRepository communicationRepository;
    private final MeetupLookupRepository meetupLookupRepository;
    private final UserStatusLookupRepository userStatusLookupRepository;

    public CommunicationService(
            CommunicationRepository communicationRepository,
            MeetupLookupRepository meetupLookupRepository,
            UserStatusLookupRepository userStatusLookupRepository
    ) {
        this.communicationRepository = communicationRepository;
        this.meetupLookupRepository = meetupLookupRepository;
        this.userStatusLookupRepository = userStatusLookupRepository;
    }

    public CommunicationResponse createCommunication(Long actorUserId, Long meetupId, CreateCommunicationRequest request) {
        ensureActorNotBlocked(actorUserId, "COMMUNICATION_CREATE_FORBIDDEN");

        Long hostUserId = meetupLookupRepository.findHostUserId(meetupId);
        if (hostUserId == null) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }

        if (!hostUserId.equals(actorUserId)) {
            throw new ApiException("COMMUNICATION_CREATE_FORBIDDEN", "Only host can create communication");
        }

        Communication communication = new Communication(meetupId, request.getContent(), OffsetDateTime.now());
        Communication saved = communicationRepository.save(communication);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<CommunicationResponse> listCommunications(Long meetupId, Pageable pageable) {
        if (!meetupLookupRepository.existsById(meetupId)) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }

        return communicationRepository
                .findByMeetupIdOrderByCreatedAtDesc(meetupId, pageable)
                .map(this::toResponse);
    }

    private void ensureActorNotBlocked(Long actorUserId, String forbiddenCode) {
        String status = userStatusLookupRepository.findStatusByUserId(actorUserId);
        if (USER_STATUS_BLOCKED.equals(status)) {
            throw new ApiException(forbiddenCode, "Blocked user cannot perform write actions");
        }
    }

    private CommunicationResponse toResponse(Communication communication) {
        return new CommunicationResponse(
                communication.getId(),
                communication.getMeetupId(),
                communication.getContent(),
                communication.getCreatedAt()
        );
    }
}
