package com.example.walkservice.communication.repository;

import com.example.walkservice.communication.entity.Communication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunicationRepository extends JpaRepository<Communication, Long> {

    Page<Communication> findByMeetupIdOrderByCreatedAtDesc(Long meetupId, Pageable pageable);
}
