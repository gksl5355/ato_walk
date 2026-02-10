package com.example.walkservice.participation.repository;

import com.example.walkservice.participation.entity.Participation;
import com.example.walkservice.participation.entity.ParticipationStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Optional<Participation> findByIdAndMeetupId(Long id, Long meetupId);

    Page<Participation> findByMeetupIdAndStatusOrderByCreatedAtDesc(Long meetupId, ParticipationStatus status, Pageable pageable);
}
