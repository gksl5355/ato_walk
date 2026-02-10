package com.example.walkservice.participation.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MeetupLookupRepository {

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
