package com.example.walkservice.communication.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.communication.dto.CommunicationResponse;
import com.example.walkservice.communication.dto.CreateCommunicationRequest;
import com.example.walkservice.communication.entity.Communication;
import com.example.walkservice.communication.repository.CommunicationRepository;
import com.example.walkservice.communication.repository.MeetupLookupRepository;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class CommunicationServiceTest {

    @Test
    void createCommunication_requiresHost() {
        CommunicationRepository communicationRepository = mock(CommunicationRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        CommunicationService service = new CommunicationService(communicationRepository, meetupLookupRepository);

        when(meetupLookupRepository.findHostUserId(1L)).thenReturn(10L);

        assertThatThrownBy(() -> service.createCommunication(20L, 1L, new CreateCommunicationRequest("hi")))
                .isInstanceOf(ApiException.class)
                .hasMessage("Only host can create communication");
    }

    @Test
    void createCommunication_requiresMeetup() {
        CommunicationRepository communicationRepository = mock(CommunicationRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        CommunicationService service = new CommunicationService(communicationRepository, meetupLookupRepository);

        when(meetupLookupRepository.findHostUserId(1L)).thenReturn(null);

        assertThatThrownBy(() -> service.createCommunication(10L, 1L, new CreateCommunicationRequest("hi")))
                .isInstanceOf(ApiException.class)
                .hasMessage("Meetup not found");
    }

    @Test
    void createCommunication_returnsSavedId() throws Exception {
        CommunicationRepository communicationRepository = mock(CommunicationRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        CommunicationService service = new CommunicationService(communicationRepository, meetupLookupRepository);

        when(meetupLookupRepository.findHostUserId(1L)).thenReturn(10L);

        Communication saved = new Communication(1L, "hello", OffsetDateTime.now());
        setId(saved, 99L);
        when(communicationRepository.save(any(Communication.class))).thenReturn(saved);

        CommunicationResponse response = service.createCommunication(10L, 1L, new CreateCommunicationRequest("hello"));
        assertThat(response.getId()).isEqualTo(99L);
    }

    @Test
    void listCommunications_requiresMeetup() {
        CommunicationRepository communicationRepository = mock(CommunicationRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        CommunicationService service = new CommunicationService(communicationRepository, meetupLookupRepository);

        when(meetupLookupRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.listCommunications(1L, PageRequest.of(0, 20)))
                .isInstanceOf(ApiException.class)
                .hasMessage("Meetup not found");
    }

    @Test
    void listCommunications_mapsResponse() throws Exception {
        CommunicationRepository communicationRepository = mock(CommunicationRepository.class);
        MeetupLookupRepository meetupLookupRepository = mock(MeetupLookupRepository.class);
        CommunicationService service = new CommunicationService(communicationRepository, meetupLookupRepository);

        when(meetupLookupRepository.existsById(1L)).thenReturn(true);

        Communication c1 = new Communication(1L, "a", OffsetDateTime.parse("2026-01-01T00:00:00Z"));
        setId(c1, 1L);
        Communication c2 = new Communication(1L, "b", OffsetDateTime.parse("2026-01-02T00:00:00Z"));
        setId(c2, 2L);

        Page<Communication> page = new PageImpl<>(List.of(c2, c1), PageRequest.of(0, 20), 2);
        when(communicationRepository.findByMeetupIdOrderByCreatedAtDesc(1L, PageRequest.of(0, 20))).thenReturn(page);

        Page<CommunicationResponse> responsePage = service.listCommunications(1L, PageRequest.of(0, 20));
        assertThat(responsePage.getTotalElements()).isEqualTo(2);
        assertThat(responsePage.getContent().getFirst().getId()).isEqualTo(2L);
        assertThat(responsePage.getContent().getFirst().getContent()).isEqualTo("b");
    }

    private static void setId(Communication communication, Long id) throws Exception {
        Field field = Communication.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(communication, id);
    }
}
