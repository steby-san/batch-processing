package com.example.batch_processing.listener;

import com.example.batch_processing.model.csv.EmployeeCsvRow;
import com.example.batch_processing.model.csv.TransactionCsvRow;
import com.example.batch_processing.model.entity.BatchErrorLog;
import com.example.batch_processing.repository.BatchErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Spring Batch {@link SkipListener} that persists error information for skipped
 * items into the {@code BATCH_ERROR_LOG} table.
 *
 * <p>Only {@link #onSkipInProcess(Object, Throwable)} is implemented — read and
 * write skips are no-ops because validation occurs exclusively in the processor.
 *
 * <p>Requirements: 4.3, 4.4, 4.5, 4.6, 4.9
 */
@Component
public class ErrorLogWriter implements SkipListener<Object, Object> {

    private static final Logger log = LoggerFactory.getLogger(ErrorLogWriter.class);

    private static final int MAX_RAW_DATA_LENGTH = 4000;
    private static final int MAX_ERROR_MESSAGE_LENGTH = 1000;

    private final BatchErrorLogRepository repository;

    private StepExecution stepExecution;

    public ErrorLogWriter(BatchErrorLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Captures the {@link StepExecution} before the step starts so that
     * {@code jobExecutionId} is available when logging errors.
     *
     * @param stepExecution the current step execution context
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    /**
     * Persists an error log record for an item that was skipped during processing.
     *
     * <p>Determines the domain from the item type, truncates raw data and error
     * message to their column limits, then saves a {@link BatchErrorLog} record.
     * Any persistence failure is logged at ERROR level and swallowed so that the
     * batch job can continue.
     *
     * @param item the item that caused the skip
     * @param t    the throwable that triggered the skip
     */
    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        String domain;
        if (item instanceof TransactionCsvRow) {
            domain = "TRANSACTION";
        } else if (item instanceof EmployeeCsvRow) {
            domain = "EMPLOYEE";
        } else {
            domain = "UNKNOWN";
        }

        Long jobExecutionId = stepExecution.getJobExecutionId();

        String rawData = truncate(item.toString(), MAX_RAW_DATA_LENGTH);

        String message = t.getMessage();
        String errorMessage = truncate(message != null ? message : "", MAX_ERROR_MESSAGE_LENGTH);

        BatchErrorLog errorLog = BatchErrorLog.builder()
                .jobExecutionId(jobExecutionId)
                .rawData(rawData)
                .errorMessage(errorMessage)
                .domain(domain)
                .createdAt(Instant.now())
                .lineNumber(null)
                .build();

        try {
            repository.save(errorLog);
        } catch (Exception e) {
            log.error("Failed to save error log for item: {}", item, e);
        }
    }

    /**
     * No-op — read-time skips do not produce a processable item, so no error log
     * can be written.
     *
     * @param t the throwable that triggered the read skip
     */
    @Override
    public void onSkipInRead(Throwable t) {
        // no-op
    }

    /**
     * No-op — write-time skips are not expected in the current processing pipeline.
     *
     * @param item the item that was skipped during writing
     * @param t    the throwable that triggered the write skip
     */
    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        // no-op
    }

    /**
     * Truncates {@code value} to at most {@code maxLength} characters.
     *
     * @param value     the string to truncate (may be null)
     * @param maxLength the maximum allowed length
     * @return the (possibly truncated) string, or {@code null} if {@code value} is null
     */
    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
    }
}
