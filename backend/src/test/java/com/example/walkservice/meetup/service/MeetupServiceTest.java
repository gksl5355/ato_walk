package com.example.walkservice.meetup.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.meetup.dto.CreateMeetupRequest;
import com.example.walkservice.meetup.dto.UpdateMeetupRequest;
import com.example.walkservice.meetup.entity.Meetup;
import com.example.walkservice.meetup.entity.MeetupStatus;
import com.example.walkservice.meetup.repository.MeetupRepository;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MeetupServiceTest {

    @Mock
    private MeetupRepository meetupRepository;

    @Mock
    private BlockedWriteGuard blockedWriteGuard;

    @InjectMocks
    private MeetupService meetupService;

    @Test
    void cancelMeetup_requiresHost() {
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
        when(meetupRepository.findById(1L)).thenReturn(Optional.of(meetup));

        assertThatThrownBy(() -> meetupService.cancelMeetup(20L, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Only host can cancel meetup");
    }

    @Test
    void endMeetup_changesStatus() {
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
        when(meetupRepository.findById(1L)).thenReturn(Optional.of(meetup));

        meetupService.endMeetup(10L, 1L);
        assertThat(meetup.getStatus()).isEqualTo(MeetupStatus.ENDED);
    }

    @Test
    void cancelMeetup_requiresRecruitingState() {
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
        when(meetupRepository.findById(1L)).thenReturn(Optional.of(meetup));

        assertThatThrownBy(() -> meetupService.cancelMeetup(10L, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Meetup can only be canceled while recruiting");
    }

    @Test
    void endMeetup_requiresRecruitingState() {
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
        when(meetupRepository.findById(1L)).thenReturn(Optional.of(meetup));

        assertThatThrownBy(() -> meetupService.endMeetup(10L, 1L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Meetup can only be ended while recruiting");
    }

    @Test
    void updateMeetup_requiresRecruitingState() {
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
        when(meetupRepository.findById(1L)).thenReturn(Optional.of(meetup));

        UpdateMeetupRequest request = new UpdateMeetupRequest(
                "t2",
                "d2",
                "loc2",
                5,
                OffsetDateTime.now().plusDays(2)
        );

        assertThatThrownBy(() -> meetupService.updateMeetup(10L, 1L, request))
                .isInstanceOf(ApiException.class)
                .hasMessage("Meetup can only be updated while recruiting");
    }

    @Test
    void createMeetup_returnsSavedId() throws Exception {
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

        when(meetupRepository.save(any(Meetup.class))).thenReturn(saved);

        CreateMeetupRequest request = new CreateMeetupRequest(
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1)
        );

        assertThat(meetupService.createMeetup(10L, request).getId()).isEqualTo(99L);
    }

    @Test
    void createMeetup_blockedActor_throwsForbidden() {
        willThrow(new ApiException("MEETUP_CREATE_FORBIDDEN", "Blocked user cannot perform write actions"))
                .given(blockedWriteGuard)
                .ensureNotBlocked(1L, "MEETUP_CREATE_FORBIDDEN");

        CreateMeetupRequest request = new CreateMeetupRequest(
                "t",
                null,
                "loc",
                3,
                OffsetDateTime.now().plusDays(1)
        );

        ApiException ex = org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> meetupService.createMeetup(1L, request)
        );
        assertThat(ex.getCode()).isEqualTo("MEETUP_CREATE_FORBIDDEN");
        verifyNoInteractions(meetupRepository);
    }

    @Test
    void cancelMeetup_blockedActor_throwsForbidden() {
        willThrow(new ApiException("MEETUP_CANCEL_FORBIDDEN", "Blocked user cannot perform write actions"))
                .given(blockedWriteGuard)
                .ensureNotBlocked(1L, "MEETUP_CANCEL_FORBIDDEN");

        ApiException ex = org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> meetupService.cancelMeetup(1L, 1L)
        );
        assertThat(ex.getCode()).isEqualTo("MEETUP_CANCEL_FORBIDDEN");
        verifyNoInteractions(meetupRepository);
    }

    @Test
    void endMeetup_blockedActor_throwsForbidden() {
        willThrow(new ApiException("MEETUP_END_FORBIDDEN", "Blocked user cannot perform write actions"))
                .given(blockedWriteGuard)
                .ensureNotBlocked(1L, "MEETUP_END_FORBIDDEN");

        ApiException ex = org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> meetupService.endMeetup(1L, 1L)
        );
        assertThat(ex.getCode()).isEqualTo("MEETUP_END_FORBIDDEN");
        verifyNoInteractions(meetupRepository);
    }

    @Test
    void updateMeetup_blockedActor_throwsForbidden() {
        willThrow(new ApiException("MEETUP_UPDATE_FORBIDDEN", "Blocked user cannot perform write actions"))
                .given(blockedWriteGuard)
                .ensureNotBlocked(1L, "MEETUP_UPDATE_FORBIDDEN");

        UpdateMeetupRequest request = new UpdateMeetupRequest(
                "t2",
                "d2",
                "loc2",
                5,
                OffsetDateTime.now().plusDays(2)
        );

        ApiException ex = org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> meetupService.updateMeetup(1L, 1L, request)
        );
        assertThat(ex.getCode()).isEqualTo("MEETUP_UPDATE_FORBIDDEN");
        verifyNoInteractions(meetupRepository);
    }

    private static void setId(Meetup meetup, Long id) throws Exception {
        Field field = Meetup.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(meetup, id);
    }
}
