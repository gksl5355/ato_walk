package com.example.walkservice.communication.repository;

import com.example.walkservice.communication.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByMeetupIdOrderByCreatedAtAsc(Long meetupId, Pageable pageable);
}
