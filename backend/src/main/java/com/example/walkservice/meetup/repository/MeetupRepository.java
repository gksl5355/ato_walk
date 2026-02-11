package com.example.walkservice.meetup.repository;

import com.example.walkservice.meetup.entity.Meetup;
import com.example.walkservice.meetup.entity.MeetupStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {
    Page<Meetup> findAllByStatus(MeetupStatus status, Pageable pageable);

    @Query(
            value = """
                    select distinct m.*
                    from meetups m
                    where m.status = :status
                      and (:dogSize is null or m.dog_size = :dogSize)
                      and (:sociabilityLevel is null or m.sociability_level = :sociabilityLevel)
                      and (:reactivityLevel is null or m.reactivity_level = :reactivityLevel)
                      and (:neutered is null or m.neutered = :neutered)
                    """,
            countQuery = """
                    select count(m.id)
                    from meetups m
                    where m.status = :status
                      and (:dogSize is null or m.dog_size = :dogSize)
                      and (:sociabilityLevel is null or m.sociability_level = :sociabilityLevel)
                      and (:reactivityLevel is null or m.reactivity_level = :reactivityLevel)
                      and (:neutered is null or m.neutered = :neutered)
                    """,
            nativeQuery = true
    )
    Page<Meetup> findAllRecruitingWithMeetupFilters(
            @Param("status") String status,
            @Param("dogSize") String dogSize,
            @Param("sociabilityLevel") String sociabilityLevel,
            @Param("reactivityLevel") String reactivityLevel,
            @Param("neutered") Boolean neutered,
            Pageable pageable
    );
}
