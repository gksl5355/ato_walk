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
                    join dogs d on d.user_id = m.host_user_id
                    where m.status = :status
                      and (:dogSize is null or d.size = :dogSize)
                      and (:sociabilityLevel is null or d.sociability_level = :sociabilityLevel)
                      and (:reactivityLevel is null or d.reactivity_level = :reactivityLevel)
                    """,
            countQuery = """
                    select count(distinct m.id)
                    from meetups m
                    join dogs d on d.user_id = m.host_user_id
                    where m.status = :status
                      and (:dogSize is null or d.size = :dogSize)
                      and (:sociabilityLevel is null or d.sociability_level = :sociabilityLevel)
                      and (:reactivityLevel is null or d.reactivity_level = :reactivityLevel)
                    """,
            nativeQuery = true
    )
    Page<Meetup> findAllRecruitingWithDogFilters(
            @Param("status") String status,
            @Param("dogSize") String dogSize,
            @Param("sociabilityLevel") String sociabilityLevel,
            @Param("reactivityLevel") String reactivityLevel,
            Pageable pageable
    );
}
