package com.example.walkservice.participation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import com.example.walkservice.participation.dto.ParticipationResponse;
import com.example.walkservice.participation.entity.Participation;
import com.example.walkservice.participation.entity.ParticipationStatus;
import com.example.walkservice.participation.repository.DogProfileLookupRepository;
import com.example.walkservice.participation.repository.MeetupLookupRepository;
import com.example.walkservice.participation.repository.ParticipationRepository;
import com.example.walkservice.meetup.entity.MeetupStatus;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    private static MeetupLookupRepository.MeetupParticipationPolicy policy(
            Long hostUserId,
            DogSize dogSize,
            DogSociabilityLevel sociabilityLevel,
            DogReactivityLevel reactivityLevel,
            Boolean neutered
    ) {
        return new MeetupLookupRepository.MeetupParticipationPolicy(
                hostUserId,
                MeetupStatus.RECRUITING,
                dogSize,
                sociabilityLevel,
                reactivityLevel,
                neutered
        );
    }

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private MeetupLookupRepository meetupLookupRepository;

    @Mock
    private DogProfileLookupRepository dogProfileLookupRepository;

    @Mock
    private BlockedWriteGuard blockedWriteGuard;

    @InjectMocks
    private ParticipationService participationService;

    @Test
    void requestParticipation_blockedActor_throwsForbidden() {
        willThrow(new ApiException("PARTICIPATION_REQUEST_FORBIDDEN", "Blocked user cannot perform write actions"))
                .given(blockedWriteGuard)
                .ensureNotBlocked(1L, "PARTICIPATION_REQUEST_FORBIDDEN");

        ApiException ex = (ApiException) org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> participationService.requestParticipation(1L, 10L)
        );
        assertThat(ex.getCode()).isEqualTo("PARTICIPATION_REQUEST_FORBIDDEN");
        then(participationRepository).shouldHaveNoInteractions();
    }

    @Test
    void requestParticipation_meetupMissing_throwsNotFound() {
        given(meetupLookupRepository.findParticipationPolicy(10L)).willReturn(null);

        ApiException ex = (ApiException) org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> participationService.requestParticipation(1L, 10L)
        );
        assertThat(ex.getCode()).isEqualTo("MEETUP_FIND_NOT_FOUND");
        then(participationRepository).shouldHaveNoInteractions();
    }

    @Test
    void requestParticipation_alreadyApproved_throwsDuplicate() {
        given(meetupLookupRepository.findParticipationPolicy(1L)).willReturn(policy(10L, null, null, null, null));
        given(participationRepository.existsByMeetupIdAndUserIdAndStatus(1L, 20L, ParticipationStatus.REQUESTED))
                .willReturn(false);
        given(participationRepository.existsByMeetupIdAndUserIdAndStatus(1L, 20L, ParticipationStatus.APPROVED))
                .willReturn(true);

        ApiException ex = (ApiException) org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> participationService.requestParticipation(20L, 1L)
        );
        assertThat(ex.getCode()).isEqualTo("PARTICIPATION_REQUEST_DUPLICATE");
    }

    @Test
    void approveParticipation_notHost_throwsForbidden() {
        given(meetupLookupRepository.findHostUserId(1L)).willReturn(10L);

        ApiException ex = (ApiException) org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> participationService.approveParticipation(20L, 1L, 100L)
        );
        assertThat(ex.getCode()).isEqualTo("PARTICIPATION_APPROVE_FORBIDDEN");
        then(participationRepository).shouldHaveNoInteractions();
    }

    @Test
    void approveParticipation_invalidState_throwsBadRequest() {
        given(meetupLookupRepository.findHostUserId(1L)).willReturn(10L);

        Participation participation = new Participation(
                1L,
                20L,
                ParticipationStatus.REJECTED,
                OffsetDateTime.now()
        );

        given(participationRepository.findByIdAndMeetupId(100L, 1L)).willReturn(Optional.of(participation));

        assertThatThrownBy(() -> participationService.approveParticipation(10L, 1L, 100L))
                .isInstanceOf(ApiException.class)
                .hasMessage("Participation can only be approved when requested");
    }

    @Test
    void listRequestedParticipations_requiresHost() {
        given(meetupLookupRepository.findHostUserId(1L)).willReturn(10L);

        ApiException ex = (ApiException) org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> participationService.listRequestedParticipations(20L, 1L, PageRequest.of(0, 20))
        );
        assertThat(ex.getCode()).isEqualTo("PARTICIPATION_LIST_FORBIDDEN");
        then(participationRepository).shouldHaveNoInteractions();
    }

    @Test
    void listRequestedParticipations_mapsResponse() throws Exception {
        given(meetupLookupRepository.findHostUserId(1L)).willReturn(10L);

        Participation p1 = new Participation(
                1L,
                20L,
                ParticipationStatus.REQUESTED,
                OffsetDateTime.parse("2026-01-01T00:00:00Z")
        );
        setId(p1, 1L);

        Participation p2 = new Participation(
                1L,
                30L,
                ParticipationStatus.REQUESTED,
                OffsetDateTime.parse("2026-01-02T00:00:00Z")
        );
        setId(p2, 2L);

        Page<Participation> page = new PageImpl<>(List.of(p2, p1), PageRequest.of(0, 20), 2);
        given(participationRepository.findByMeetupIdAndStatusOrderByCreatedAtDesc(
                1L,
                ParticipationStatus.REQUESTED,
                PageRequest.of(0, 20)
        )).willReturn(page);

        Page<ParticipationResponse> responsePage = participationService.listRequestedParticipations(
                10L,
                1L,
                PageRequest.of(0, 20)
        );
        assertThat(responsePage.getTotalElements()).isEqualTo(2);
        assertThat(responsePage.getContent().getFirst().getId()).isEqualTo(2L);
        assertThat(responsePage.getContent().getFirst().getUserId()).isEqualTo(30L);
        assertThat(responsePage.getContent().getFirst().getStatus()).isEqualTo(ParticipationStatus.REQUESTED);
    }

    private static void setId(Participation participation, Long id) throws Exception {
        Field field = Participation.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(participation, id);
    }

    @Test
    void requestParticipation_notEligible_throwsBadRequest() {
        given(meetupLookupRepository.findParticipationPolicy(1L)).willReturn(policy(10L, DogSize.SMALL, null, null, null));
        given(participationRepository.existsByMeetupIdAndUserIdAndStatus(1L, 20L, ParticipationStatus.REQUESTED))
                .willReturn(false);
        given(participationRepository.existsByMeetupIdAndUserIdAndStatus(1L, 20L, ParticipationStatus.APPROVED))
                .willReturn(false);
        given(dogProfileLookupRepository.findPrimaryDogProfileByUserId(20L)).willReturn(
                new DogProfileLookupRepository.DogProfile(DogSize.MEDIUM, DogSociabilityLevel.MEDIUM, DogReactivityLevel.LOW, true)
        );

        ApiException ex = (ApiException) org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> participationService.requestParticipation(20L, 1L)
        );
        assertThat(ex.getCode()).isEqualTo("PARTICIPATION_REQUEST_NOT_ELIGIBLE");
    }
}
