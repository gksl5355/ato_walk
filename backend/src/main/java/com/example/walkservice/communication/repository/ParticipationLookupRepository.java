package com.example.walkservice.communication.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository("communicationParticipationLookupRepository")
public class ParticipationLookupRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public boolean existsApprovedByMeetupIdAndUserId(Long meetupId, Long userId) {
        Object value = entityManager
                .createNativeQuery(
                        "select count(1) from participations where meetup_id = :meetupId and user_id = :userId and status = 'APPROVED'"
                )
                .setParameter("meetupId", meetupId)
                .setParameter("userId", userId)
                .getSingleResult();

        if (value instanceof Number number) {
            return number.longValue() > 0;
        }
        return Long.parseLong(value.toString()) > 0;
    }
}
