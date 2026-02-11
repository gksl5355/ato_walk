package com.example.walkservice.participation.repository;

import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import com.example.walkservice.meetup.entity.MeetupStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository("participationMeetupLookupRepository")
public class MeetupLookupRepository {

    public record MeetupParticipationPolicy(
            Long hostUserId,
            MeetupStatus status,
            DogSize dogSize,
            DogSociabilityLevel sociabilityLevel,
            DogReactivityLevel reactivityLevel,
            Boolean neutered
    ) {
    }

    @PersistenceContext
    private EntityManager entityManager;

    public Long findHostUserId(Long meetupId) {
        List<?> rows = entityManager
                .createNativeQuery("select host_user_id from meetups where id = :meetupId")
                .setParameter("meetupId", meetupId)
                .getResultList();

        if (rows.isEmpty()) {
            return null;
        }

        Object value = rows.getFirst();
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    public MeetupParticipationPolicy findParticipationPolicy(Long meetupId) {
        List<?> rows = entityManager
                .createNativeQuery(
                        """
                                select host_user_id, status, dog_size, sociability_level, reactivity_level, neutered
                                from meetups
                                where id = :meetupId
                                """
                )
                .setParameter("meetupId", meetupId)
                .getResultList();

        if (rows.isEmpty()) {
            return null;
        }

        Object[] row = (Object[]) rows.getFirst();
        Long hostUserId = toLong(row[0]);
        MeetupStatus status = row[1] == null ? null : MeetupStatus.valueOf(row[1].toString());
        DogSize dogSize = row[2] == null ? null : DogSize.valueOf(row[2].toString());
        DogSociabilityLevel sociabilityLevel = row[3] == null ? null : DogSociabilityLevel.valueOf(row[3].toString());
        DogReactivityLevel reactivityLevel = row[4] == null ? null : DogReactivityLevel.valueOf(row[4].toString());
        Boolean neutered = row[5] == null ? null : Boolean.valueOf(row[5].toString());
        return new MeetupParticipationPolicy(hostUserId, status, dogSize, sociabilityLevel, reactivityLevel, neutered);
    }

    private Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(value.toString());
    }

    public boolean existsById(Long meetupId) {
        Object value = entityManager
                .createNativeQuery("select count(1) from meetups where id = :meetupId")
                .setParameter("meetupId", meetupId)
                .getSingleResult();

        if (value instanceof Number number) {
            return number.longValue() > 0;
        }
        return Long.parseLong(value.toString()) > 0;
    }
}
