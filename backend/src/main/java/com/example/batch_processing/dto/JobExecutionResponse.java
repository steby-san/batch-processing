package com.example.batch_processing.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO representing the response for a job execution query.
 * Satisfies Requirements 5.2.
 */
@Data
@Builder
public class JobExecutionResponse {

    /** Spring Batch JobExecution ID. */
    private Long executionId;

    /** Batch status string (e.g. COMPLETED, FAILED, STARTED). */
    private String status;

    /** Number of items successfully written during the execution. */
    private int writeCount;

    /** Number of items skipped during the execution. */
    private int skipCount;

    /** Job start time in ISO-8601 format. */
    private String startTime;

    /** Job end time in ISO-8601 format, or null if the job has not finished. */
    private String endTime;
}
