package com.example.walkservice.participation.repository;

import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository("participationDogProfileLookupRepository")
public class DogProfileLookupRepository {

    public record DogProfile(
            DogSize size,
            DogSociabilityLevel sociabilityLevel,
            DogReactivityLevel reactivityLevel,
            Boolean neutered
    ) {
    }

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

    public DogProfile findPrimaryDogProfileByUserId(Long userId) {
        List<?> rows = entityManager
                .createNativeQuery(
                        """
                                select size, sociability_level, reactivity_level, neutered
                                from dogs
                                where user_id = :userId
                                order by created_at asc
                                limit 1
                                """
                )
                .setParameter("userId", userId)
                .getResultList();

        if (rows.isEmpty()) {
            return null;
        }

        Object[] row = (Object[]) rows.getFirst();
        DogSize size = row[0] == null ? null : DogSize.valueOf(row[0].toString());
        DogSociabilityLevel sociabilityLevel = row[1] == null ? null : DogSociabilityLevel.valueOf(row[1].toString());
        DogReactivityLevel reactivityLevel = row[2] == null ? null : DogReactivityLevel.valueOf(row[2].toString());
        Boolean neutered = row[3] == null ? null : Boolean.valueOf(row[3].toString());
        return new DogProfile(size, sociabilityLevel, reactivityLevel, neutered);
    }
}
