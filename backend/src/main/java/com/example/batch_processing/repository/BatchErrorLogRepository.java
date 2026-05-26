package com.example.batch_processing.repository;

import com.example.batch_processing.model.entity.BatchErrorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link BatchErrorLog} entities.
 *
 * <p>Used by {@code ErrorLogWriter} to persist error records for skipped items
 * during Spring Batch job execution.
 *
 * <p>Requirements: 4.3, 4.9
 */
@Repository
public interface BatchErrorLogRepository extends JpaRepository<BatchErrorLog, Long> {
}
