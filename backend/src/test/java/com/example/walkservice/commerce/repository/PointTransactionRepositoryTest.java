package com.example.walkservice.commerce.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.walkservice.commerce.entity.PointTransaction;
import com.example.walkservice.commerce.entity.PointTransactionType;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PointTransactionRepositoryTest {

    @Autowired
    private PointTransactionRepository pointTransactionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void saveAndQuery_preservesPointTransactionIntegrity() {
        jdbcTemplate.update(
                """
                insert into users (email, password_hash, status, created_at, point_balance)
                values (?, ?, ?, ?, ?)
                on conflict (email) do update set point_balance = excluded.point_balance
                """,
                "point-test-user@local",
                "hashed",
                "ACTIVE",
                OffsetDateTime.now(),
                100000L
        );

        Long userId = jdbcTemplate.queryForObject(
                "select id from users where email = ?",
                Long.class,
                "point-test-user@local"
        );

        PointTransaction saved = pointTransactionRepository.save(new PointTransaction(
                userId,
                null,
                PointTransactionType.SPEND,
                1000L,
                99000L,
                OffsetDateTime.now()
        ));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAmount()).isEqualTo(1000L);
        assertThat(saved.getBalanceAfter()).isEqualTo(99000L);

        var page = pointTransactionRepository.findAllByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 20));
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(1);
        assertThat(page.getContent().getFirst().getUserId()).isEqualTo(userId);
    }
}
