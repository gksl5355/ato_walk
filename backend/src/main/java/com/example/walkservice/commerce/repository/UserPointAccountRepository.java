package com.example.walkservice.commerce.repository;

import com.example.walkservice.commerce.entity.UserPointAccount;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserPointAccountRepository extends JpaRepository<UserPointAccount, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserPointAccount u where u.id = :userId")
    Optional<UserPointAccount> findByIdForUpdate(@Param("userId") Long userId);
}
