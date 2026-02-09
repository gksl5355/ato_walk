package com.example.walkservice.dog.repository;

import com.example.walkservice.dog.entity.Dog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DogRepository extends JpaRepository<Dog, Long> {
    Optional<Dog> findByIdAndUserId(Long id, Long userId);
}
