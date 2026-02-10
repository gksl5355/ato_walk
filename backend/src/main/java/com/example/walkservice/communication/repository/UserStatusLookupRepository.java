package com.example.walkservice.communication.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class UserStatusLookupRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public String findStatusByUserId(Long userId) {
        try {
            Object result = entityManager.createNativeQuery("select status from users where id = :userId")
                    .setParameter("userId", userId)
                    .getSingleResult();
            return result == null ? null : result.toString();
        } catch (NoResultException ex) {
            return null;
        }
    }
}
