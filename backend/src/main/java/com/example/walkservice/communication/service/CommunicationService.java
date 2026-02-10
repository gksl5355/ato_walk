package com.example.walkservice.communication.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.communication.dto.CommunicationResponse;
import com.example.walkservice.communication.dto.CreateCommunicationRequest;
import com.example.walkservice.communication.entity.Communication;
import com.example.walkservice.communication.repository.CommunicationRepository;
import com.example.walkservice.communication.repository.MeetupLookupRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommunicationService {

    private final CommunicationRepository communicationRepository;
    private final MeetupLookupRepository meetupLookupRepository;
    private final BlockedWriteGuard blockedWriteGuard;

    public CommunicationService(
            CommunicationRepository communicationRepository,
            MeetupLookupRepository meetupLookupRepository,
            BlockedWriteGuard blockedWriteGuard
    ) {
        this.communicationRepository = communicationRepository;
        this.meetupLookupRepository = meetupLookupRepository;
        this.blockedWriteGuard = blockedWriteGuard;
    }

    public CommunicationResponse createCommunication(Long actorUserId, Long meetupId, CreateCommunicationRequest request) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "COMMUNICATION_CREATE_FORBIDDEN");

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

    private CommunicationResponse toResponse(Communication communication) {
        return new CommunicationResponse(
                communication.getId(),
                communication.getMeetupId(),
                communication.getContent(),
                communication.getCreatedAt()
        );
    }
}
