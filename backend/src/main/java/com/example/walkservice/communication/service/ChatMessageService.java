package com.example.walkservice.communication.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.communication.dto.ChatMessageResponse;
import com.example.walkservice.communication.dto.CreateChatMessageRequest;
import com.example.walkservice.communication.entity.ChatMessage;
import com.example.walkservice.communication.repository.ChatMessageRepository;
import com.example.walkservice.communication.repository.DogProfileLookupRepository;
import com.example.walkservice.communication.repository.MeetupLookupRepository;
import com.example.walkservice.communication.repository.ParticipationLookupRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final MeetupLookupRepository meetupLookupRepository;
    private final ParticipationLookupRepository participationLookupRepository;
    private final DogProfileLookupRepository dogProfileLookupRepository;
    private final BlockedWriteGuard blockedWriteGuard;

    public ChatMessageService(
            ChatMessageRepository chatMessageRepository,
            MeetupLookupRepository meetupLookupRepository,
            ParticipationLookupRepository participationLookupRepository,
            DogProfileLookupRepository dogProfileLookupRepository,
            BlockedWriteGuard blockedWriteGuard
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.meetupLookupRepository = meetupLookupRepository;
        this.participationLookupRepository = participationLookupRepository;
        this.dogProfileLookupRepository = dogProfileLookupRepository;
        this.blockedWriteGuard = blockedWriteGuard;
    }

    public ChatMessageResponse createMessage(Long actorUserId, Long meetupId, CreateChatMessageRequest request) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "CHAT_CREATE_FORBIDDEN");

        Long hostUserId = meetupLookupRepository.findHostUserId(meetupId);
        if (hostUserId == null) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }

        boolean canChat = hostUserId.equals(actorUserId)
                || participationLookupRepository.existsApprovedByMeetupIdAndUserId(meetupId, actorUserId);
        if (!canChat) {
            throw new ApiException("CHAT_CREATE_FORBIDDEN", "Only host or approved participant can chat");
        }

        String nickname = dogProfileLookupRepository.findPrimaryDogNameByUserId(actorUserId);
        if (nickname == null || nickname.isBlank()) {
            nickname = "User #" + actorUserId;
        }

        ChatMessage message = new ChatMessage(
                meetupId,
                actorUserId,
                nickname,
                request.getContent(),
                OffsetDateTime.now()
        );
        ChatMessage saved = chatMessageRepository.save(message);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ChatMessageResponse> listMessages(Long actorUserId, Long meetupId, Pageable pageable) {
        Long hostUserId = meetupLookupRepository.findHostUserId(meetupId);
        if (hostUserId == null) {
            throw new ApiException("MEETUP_FIND_NOT_FOUND", "Meetup not found");
        }

        boolean canRead = hostUserId.equals(actorUserId)
                || participationLookupRepository.existsApprovedByMeetupIdAndUserId(meetupId, actorUserId);
        if (!canRead) {
            throw new ApiException("CHAT_LIST_FORBIDDEN", "Only host or approved participant can read chat");
        }

        return chatMessageRepository.findByMeetupIdOrderByCreatedAtAsc(meetupId, pageable).map(this::toResponse);
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getMeetupId(),
                message.getSenderUserId(),
                message.getSenderNickname(),
                message.getContent(),
                message.getCreatedAt()
        );
    }
}
