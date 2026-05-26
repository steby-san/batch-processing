package com.example.batch_processing.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA Entity mapping to the BATCH_ERROR_LOG table.
 * Records information about items skipped during Batch processing due to
 * ValidationException, enabling post-run error analysis without interrupting
 * the Batch job.
 *
 * <p>Requirements: 4.3, 4.4, 4.5, 4.6</p>
 */
@Entity
@Table(name = "BATCH_ERROR_LOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchErrorLog {

    /** Auto-increment primary key. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The Spring Batch job execution ID associated with this error.
     * Never null — every error log must be traceable to a specific job run.
     */
    @Column(name = "job_execution_id", nullable = false)
    private Long jobExecutionId;

    /**
     * The 1-based line number in the source file where the error occurred.
     * Nullable — may not be available in all reader configurations.
     */
    @Column(name = "line_number")
    private Integer lineNumber;

    /**
     * The raw string representation of the item that failed validation.
     * Truncated to 4000 characters before persisting if the original is longer.
     */
    @Column(name = "raw_data", length = 4000)
    private String rawData;

    /**
     * The validation error message from the ValidationException.
     * Truncated to 1000 characters before persisting if the original is longer.
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    /**
     * The processing domain that produced the error.
     * Expected values: {@code "TRANSACTION"} or {@code "EMPLOYEE"}.
     */
    @Column(name = "domain", nullable = false, length = 20)
    private String domain;

    /**
     * UTC timestamp of when this error log record was created.
     * Set at write time by ErrorLogWriter.
     */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
