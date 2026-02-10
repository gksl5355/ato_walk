package com.example.walkservice.participation.repository;

import com.example.walkservice.participation.entity.Participation;
import com.example.walkservice.participation.entity.ParticipationStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Optional<Participation> findByIdAndMeetupId(Long id, Long meetupId);

    Optional<Participation> findTopByMeetupIdAndUserIdOrderByCreatedAtDesc(Long meetupId, Long userId);

    boolean existsByMeetupIdAndUserIdAndStatus(Long meetupId, Long userId, ParticipationStatus status);

    Page<Participation> findByMeetupIdAndStatusOrderByCreatedAtDesc(Long meetupId, ParticipationStatus status, Pageable pageable);

    Page<Participation> findByMeetupIdAndStatusOrderByCreatedAtAsc(Long meetupId, ParticipationStatus status, Pageable pageable);
}
