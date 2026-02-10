package com.example.walkservice.communication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.communication.dto.ChatMessageResponse;
import com.example.walkservice.communication.dto.CreateChatMessageRequest;
import com.example.walkservice.communication.entity.ChatMessage;
import com.example.walkservice.communication.repository.ChatMessageRepository;
import com.example.walkservice.communication.repository.DogProfileLookupRepository;
import com.example.walkservice.communication.repository.MeetupLookupRepository;
import com.example.walkservice.communication.repository.ParticipationLookupRepository;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class ChatMessageServiceTest {

    @Test
    void createMessage_requiresHostOrApprovedParticipant() {
        ChatMessageRepository chatMessageRepository = mock(ChatMessageRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        ParticipationLookupRepository participationLookupRepository = mock(ParticipationLookupRepository.class);
        DogProfileLookupRepository dogProfileLookupRepository = mock(DogProfileLookupRepository.class);
        BlockedWriteGuard blockedWriteGuard = mock(BlockedWriteGuard.class);

        ChatMessageService service = new ChatMessageService(
                chatMessageRepository,
                meetupLookupRepository,
                participationLookupRepository,
                dogProfileLookupRepository,
                blockedWriteGuard
        );

        when(meetupLookupRepository.findHostUserId(1L)).thenReturn(10L);
        when(participationLookupRepository.existsApprovedByMeetupIdAndUserId(1L, 20L)).thenReturn(false);

        assertThatThrownBy(() -> service.createMessage(20L, 1L, new CreateChatMessageRequest("hello")))
                .isInstanceOf(ApiException.class)
                .hasMessage("Only host or approved participant can chat");
    }

    @Test
    void createMessage_usesDogNickname() throws Exception {
        ChatMessageRepository chatMessageRepository = mock(ChatMessageRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        ParticipationLookupRepository participationLookupRepository = mock(ParticipationLookupRepository.class);
        DogProfileLookupRepository dogProfileLookupRepository = mock(DogProfileLookupRepository.class);
        BlockedWriteGuard blockedWriteGuard = mock(BlockedWriteGuard.class);

        ChatMessageService service = new ChatMessageService(
                chatMessageRepository,
                meetupLookupRepository,
                participationLookupRepository,
                dogProfileLookupRepository,
                blockedWriteGuard
        );

        when(meetupLookupRepository.findHostUserId(1L)).thenReturn(10L);
        when(dogProfileLookupRepository.findPrimaryDogNameByUserId(10L)).thenReturn("Ato");

        ChatMessage saved = new ChatMessage(1L, 10L, "Ato", "hello", OffsetDateTime.now());
        setId(saved, 7L);
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(saved);

        ChatMessageResponse response = service.createMessage(10L, 1L, new CreateChatMessageRequest("hello"));
        assertThat(response.getId()).isEqualTo(7L);
        assertThat(response.getSenderNickname()).isEqualTo("Ato");
    }

    @Test
    void listMessages_requiresPermission() {
        ChatMessageRepository chatMessageRepository = mock(ChatMessageRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        ParticipationLookupRepository participationLookupRepository = mock(ParticipationLookupRepository.class);
        DogProfileLookupRepository dogProfileLookupRepository = mock(DogProfileLookupRepository.class);
        BlockedWriteGuard blockedWriteGuard = mock(BlockedWriteGuard.class);

        ChatMessageService service = new ChatMessageService(
                chatMessageRepository,
                meetupLookupRepository,
                participationLookupRepository,
                dogProfileLookupRepository,
                blockedWriteGuard
        );

        when(meetupLookupRepository.findHostUserId(1L)).thenReturn(10L);
        when(participationLookupRepository.existsApprovedByMeetupIdAndUserId(1L, 20L)).thenReturn(false);

        assertThatThrownBy(() -> service.listMessages(20L, 1L, PageRequest.of(0, 20)))
                .isInstanceOf(ApiException.class)
                .hasMessage("Only host or approved participant can read chat");
    }

    @Test
    void listMessages_mapsResponse() throws Exception {
        ChatMessageRepository chatMessageRepository = mock(ChatMessageRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        ParticipationLookupRepository participationLookupRepository = mock(ParticipationLookupRepository.class);
        DogProfileLookupRepository dogProfileLookupRepository = mock(DogProfileLookupRepository.class);
        BlockedWriteGuard blockedWriteGuard = mock(BlockedWriteGuard.class);

        ChatMessageService service = new ChatMessageService(
                chatMessageRepository,
                meetupLookupRepository,
                participationLookupRepository,
                dogProfileLookupRepository,
                blockedWriteGuard
        );

        when(meetupLookupRepository.findHostUserId(1L)).thenReturn(10L);

        ChatMessage m1 = new ChatMessage(1L, 10L, "Ato", "hello", OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        ChatMessage m2 = new ChatMessage(1L, 20L, "Bori", "hi", OffsetDateTime.parse("2026-01-02T00:00:00Z"));
        setId(m1, 1L);
        setId(m2, 2L);

        Page<ChatMessage> page = new PageImpl<>(List.of(m1, m2), PageRequest.of(0, 20), 2);
        when(chatMessageRepository.findByMeetupIdOrderByCreatedAtAsc(1L, PageRequest.of(0, 20))).thenReturn(page);

        Page<ChatMessageResponse> responses = service.listMessages(10L, 1L, PageRequest.of(0, 20));
        assertThat(responses.getTotalElements()).isEqualTo(2);
        assertThat(responses.getContent().getFirst().getSenderNickname()).isEqualTo("Ato");
    }

    private static void setId(ChatMessage message, Long id) throws Exception {
        Field field = ChatMessage.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(message, id);
    }
}
