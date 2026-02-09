package com.example.walkservice.meetup.repository;

import com.example.walkservice.meetup.entity.ExampleMeetup;
import com.example.walkservice.meetup.entity.ExampleMeetupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleMeetupRepository extends JpaRepository<ExampleMeetup, Long> {
    Page<ExampleMeetup> findAllByStatus(ExampleMeetupStatus status, Pageable pageable);
}
