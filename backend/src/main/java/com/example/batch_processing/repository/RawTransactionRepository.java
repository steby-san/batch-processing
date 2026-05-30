package com.example.batch_processing.repository;

import com.example.batch_processing.model.RawTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface RawTransactionRepository
        extends JpaRepository<RawTransaction, Long> {

    List<RawTransaction> findByStatus(String status);

    long countByStatus(String status);

    @Query("""
            SELECT COALESCE(SUM(t.amount),0)
            FROM RawTransaction t
            WHERE t.status='SUCCESS'
            """)
    BigDecimal getTotalRevenue();

    @Query("""
            SELECT COUNT(t)
            FROM RawTransaction t
            WHERE t.createdAt BETWEEN :start AND :end
            """)
    Long countByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
            SELECT COALESCE(SUM(t.amount),0)
            FROM RawTransaction t
            WHERE t.createdAt BETWEEN :start AND :end
            """)
    BigDecimal sumRevenueByDateRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}