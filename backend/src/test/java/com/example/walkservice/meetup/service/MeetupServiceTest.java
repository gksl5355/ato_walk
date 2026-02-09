package com.example.walkservice.meetup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.meetup.dto.CreateMeetupRequest;
import com.example.walkservice.meetup.dto.UpdateMeetupRequest;
import com.example.walkservice.meetup.entity.Meetup;
import com.example.walkservice.meetup.entity.MeetupStatus;
import com.example.walkservice.meetup.repository.MeetupRepository;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MeetupServiceTest {

    @Test
    void cancelMeetup_requiresHost() {
        MeetupRepository repo = mock(MeetupRepository.class);
        MeetupService service = new MeetupService(repo);

        Meetup meetup = new Meetup(
                10L,
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1),
                MeetupStatus.RECRUITING,
                OffsetDateTime.now()
        );
        when(repo.findById(1L)).thenReturn(Optional.of(meetup));

        assertThatThrownBy(() -> service.cancelMeetup(20L, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Only host can cancel meetup");
    }

    @Test
    void endMeetup_changesStatus() {
        MeetupRepository repo = mock(MeetupRepository.class);
        MeetupService service = new MeetupService(repo);

        Meetup meetup = new Meetup(
                10L,
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1),
                MeetupStatus.RECRUITING,
                OffsetDateTime.now()
        );
        when(repo.findById(1L)).thenReturn(Optional.of(meetup));

        service.endMeetup(10L, 1L);
        assertThat(meetup.getStatus()).isEqualTo(MeetupStatus.ENDED);
    }

    @Test
    void cancelMeetup_requiresRecruitingState() {
        MeetupRepository repo = mock(MeetupRepository.class);
        MeetupService service = new MeetupService(repo);

        Meetup meetup = new Meetup(
                10L,
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1),
                MeetupStatus.ENDED,
                OffsetDateTime.now()
        );
        when(repo.findById(1L)).thenReturn(Optional.of(meetup));

        assertThatThrownBy(() -> service.cancelMeetup(10L, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Meetup can only be canceled while recruiting");
    }

    @Test
    void endMeetup_requiresRecruitingState() {
        MeetupRepository repo = mock(MeetupRepository.class);
        MeetupService service = new MeetupService(repo);

        Meetup meetup = new Meetup(
                10L,
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1),
                MeetupStatus.CANCELED,
                OffsetDateTime.now()
        );
        when(repo.findById(1L)).thenReturn(Optional.of(meetup));

        assertThatThrownBy(() -> service.endMeetup(10L, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Meetup can only be ended while recruiting");
    }

    @Test
    void updateMeetup_requiresRecruitingState() {
        MeetupRepository repo = mock(MeetupRepository.class);
        MeetupService service = new MeetupService(repo);

        Meetup meetup = new Meetup(
                10L,
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1),
                MeetupStatus.ENDED,
                OffsetDateTime.now()
        );
        when(repo.findById(1L)).thenReturn(Optional.of(meetup));

        UpdateMeetupRequest request = new UpdateMeetupRequest(
                "t2",
                "d2",
                "loc2",
                5,
                OffsetDateTime.now().plusDays(2)
        );

        assertThatThrownBy(() -> service.updateMeetup(10L, 1L, request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Meetup can only be updated while recruiting");
    }

    @Test
    void createMeetup_returnsSavedId() throws Exception {
        MeetupRepository repo = mock(MeetupRepository.class);
        MeetupService service = new MeetupService(repo);

        Meetup saved = new Meetup(
                10L,
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1),
                MeetupStatus.RECRUITING,
                OffsetDateTime.now()
        );
        setId(saved, 99L);

        when(repo.save(any(Meetup.class))).thenReturn(saved);

        CreateMeetupRequest request = new CreateMeetupRequest(
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1)
        );

        assertThat(service.createMeetup(10L, request).getId()).isEqualTo(99L);
    }

    private static void setId(Meetup meetup, Long id) throws Exception {
        Field field = Meetup.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(meetup, id);
    }
}
