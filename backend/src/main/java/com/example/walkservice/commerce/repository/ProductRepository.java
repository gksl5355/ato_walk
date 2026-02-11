package com.example.walkservice.commerce.repository;

import com.example.walkservice.commerce.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(
            value = """
                    select *
                    from products p
                    where (:category is null or p.category = :category)
                      and (:status is null or p.status = :status)
                    order by p.created_at desc
                    """,
            countQuery = """
                    select count(p.id)
                    from products p
                    where (:category is null or p.category = :category)
                      and (:status is null or p.status = :status)
                    """,
            nativeQuery = true
    )
    Page<Product> findAllWithFilters(@Param("category") String category, @Param("status") String status, Pageable pageable);

    List<Product> findAllByIdIn(Collection<Long> ids);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id in :ids")
    List<Product> findAllByIdInForUpdate(@Param("ids") Collection<Long> ids);
}
