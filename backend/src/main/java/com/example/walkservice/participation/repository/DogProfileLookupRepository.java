package com.example.walkservice.participation.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository("participationDogProfileLookupRepository")
public class DogProfileLookupRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public String findPrimaryDogNameByUserId(Long userId) {
        List<?> rows = entityManager
                .createNativeQuery("select name from dogs where user_id = :userId order by created_at asc limit 1")
                .setParameter("userId", userId)
                .getResultList();

        if (rows.isEmpty()) {
            return null;
        }
        return rows.getFirst().toString();
    }
}
