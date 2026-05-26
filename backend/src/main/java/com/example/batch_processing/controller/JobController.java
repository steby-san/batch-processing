package com.example.batch_processing.controller;

import com.example.batch_processing.dto.JobExecutionResponse;
import org.springframework.batch.core.repository.explore.JobExplorer;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * REST controller for querying Spring Batch job execution status.
 * Satisfies Requirements 5.2, 5.3, 5.4.
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobExplorer jobExplorer;

    public JobController(JobExplorer jobExplorer) {
        this.jobExplorer = jobExplorer;
    }

    /**
     * GET /api/jobs/{executionId}
     * Returns the job execution details for the given execution ID.
     *
     * @param executionId the Spring Batch job execution ID
     * @return 200 with JobExecutionResponse, or 404 if not found
     */
    @GetMapping("/{executionId}")
    public ResponseEntity<?> getJobExecution(@PathVariable Long executionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(executionId);

        if (jobExecution == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "Job execution not found: " + executionId));
        }

        int writeCount = (int) jobExecution.getStepExecutions().stream()
                .mapToLong(step -> step.getWriteCount())
                .sum();

        int skipCount = (int) jobExecution.getStepExecutions().stream()
                .mapToLong(step -> step.getSkipCount())
                .sum();

        String startTime = jobExecution.getStartTime() != null
                ? jobExecution.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null;

        String endTime = jobExecution.getEndTime() != null
                ? jobExecution.getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null;

        JobExecutionResponse response = JobExecutionResponse.builder()
                .executionId(jobExecution.getId())
                .status(jobExecution.getStatus().toString())
                .writeCount(writeCount)
                .skipCount(skipCount)
                .startTime(startTime)
                .endTime(endTime)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Handles type mismatch when executionId cannot be parsed as a Long.
     *
     * @return 400 with error message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(400)
                .body(Map.of("message", "Invalid executionId: must be a valid integer"));
    }
}
