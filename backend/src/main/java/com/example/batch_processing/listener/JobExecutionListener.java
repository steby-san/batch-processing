package com.example.batch_processing.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;
import org.springframework.batch.core.listener.JobExecutionListener;

/**
 * Spring Batch {@link org.springframework.batch.core.listener.JobExecutionListener}
 * that logs job statistics (skipCount and writeCount) after each job completes.
 *
 * <p>Counts are summed across all step executions belonging to the job.
 *
 * <p>Validates: Requirements 5.1
 */
@Component
public class JobExecutionListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobExecutionListener.class);

    /**
     * Called by Spring Batch after the job finishes (regardless of status).
     * Sums {@code skipCount} and {@code writeCount} across all step executions
     * and logs both values at INFO level.
     *
     * @param jobExecution the completed job execution
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        long totalSkipCount = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getSkipCount)
                .sum();

        long totalWriteCount = jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum();

        log.info("Job '{}' (executionId={}) finished with status={}: writeCount={}, skipCount={}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getId(),
                jobExecution.getStatus(),
                totalWriteCount,
                totalSkipCount);
    }
}
