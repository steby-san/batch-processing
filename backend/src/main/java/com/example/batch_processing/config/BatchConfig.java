package com.example.batch_processing.config;

import com.example.batch_processing.exception.ValidationException;
import com.example.batch_processing.listener.ErrorLogWriter;
import com.example.batch_processing.listener.JobExecutionListener;
import com.example.batch_processing.model.csv.EmployeeCsvRow;
import com.example.batch_processing.model.csv.TransactionCsvRow;
import com.example.batch_processing.model.entity.RawEmployee;
import com.example.batch_processing.model.entity.RawTransaction;
import com.example.batch_processing.processor.EmployeeProcessor;
import com.example.batch_processing.processor.TransactionProcessor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch configuration that wires together the two batch jobs:
 * <ul>
 *   <li>{@code transactionJob} — reads {@link TransactionCsvRow}, processes via
 *       {@link TransactionProcessor}, writes {@link RawTransaction}</li>
 *   <li>{@code employeeJob} — reads {@link EmployeeCsvRow}, processes via
 *       {@link EmployeeProcessor}, writes {@link RawEmployee}</li>
 * </ul>
 *
 * <p>Both steps are configured with fault-tolerant skip policy:
 * {@link ValidationException} is skipped up to {@code batch.skip-limit} times.
 * {@link ErrorLogWriter} is registered as a {@link SkipListener} on each step.
 *
 * <p>Validates: Requirements 6.1, 6.2, 6.3, 6.4, 6.5, 6.6
 */
@Configuration
public class BatchConfig {

    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);

    // -----------------------------------------------------------------------
    // Configuration properties
    // -----------------------------------------------------------------------

    /**
     * Maximum number of items that may be skipped per step before the job fails.
     * Defaults to 100. Must be >= 1.
     *
     * <p>Requirement 6.4: skipLimit must be a positive integer.
     */
    @Value("${batch.skip-limit:100}")
    private int skipLimit;

    // -----------------------------------------------------------------------
    // Infrastructure beans (provided by Spring Batch auto-configuration)
    // -----------------------------------------------------------------------

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // -----------------------------------------------------------------------
    // Processor beans (implemented by Member 2 — always present)
    // -----------------------------------------------------------------------

    private final TransactionProcessor transactionProcessor;
    private final EmployeeProcessor employeeProcessor;

    // -----------------------------------------------------------------------
    // Listener beans (implemented by Member 2 — always present)
    // -----------------------------------------------------------------------

    private final ErrorLogWriter errorLogWriter;
    private final JobExecutionListener jobExecutionListener;

    // -----------------------------------------------------------------------
    // Reader beans (provided by Member 1 — may not be present yet)
    // -----------------------------------------------------------------------

    @Autowired(required = false)
    private ItemReader<TransactionCsvRow> transactionReader;

    @Autowired(required = false)
    private ItemReader<EmployeeCsvRow> employeeReader;

    // -----------------------------------------------------------------------
    // Writer beans (provided by Member 3 — may not be present yet)
    // -----------------------------------------------------------------------

    @Autowired(required = false)
    private ItemWriter<RawTransaction> transactionWriter;

    @Autowired(required = false)
    private ItemWriter<RawEmployee> employeeWriter;

    // -----------------------------------------------------------------------
    // Constructor injection for mandatory dependencies
    // -----------------------------------------------------------------------

    public BatchConfig(JobRepository jobRepository,
                       PlatformTransactionManager transactionManager,
                       TransactionProcessor transactionProcessor,
                       EmployeeProcessor employeeProcessor,
                       ErrorLogWriter errorLogWriter,
                       JobExecutionListener jobExecutionListener) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.transactionProcessor = transactionProcessor;
        this.employeeProcessor = employeeProcessor;
        this.errorLogWriter = errorLogWriter;
        this.jobExecutionListener = jobExecutionListener;
    }

    // -----------------------------------------------------------------------
    // Validation
    // -----------------------------------------------------------------------

    /**
     * Validates that {@code skipLimit} is a positive integer.
     * If not, the application context will fail to start.
     *
     * <p>Requirement 6.4: invalid skipLimit prevents startup.
     */
    @PostConstruct
    public void validateSkipLimit() {
        if (skipLimit < 1) {
            throw new IllegalStateException(
                    "batch.skip-limit must be >= 1, but was: " + skipLimit);
        }
        log.info("BatchConfig initialized with skipLimit={}", skipLimit);
    }

    // -----------------------------------------------------------------------
    // Step definitions
    // -----------------------------------------------------------------------

    /**
     * Builds the transaction processing step with fault-tolerant skip policy.
     *
     * <p>Requirements 6.1, 6.2, 6.3, 6.5: chunk-oriented step with skip on
     * {@link ValidationException}, skipLimit, and {@link ErrorLogWriter} listener.
     *
     * @return the configured {@link Step}
     */
    @Bean
    public Step transactionStep() {
        ItemReader<TransactionCsvRow> reader = transactionReader != null
                ? transactionReader
                : stubReader("transactionReader");

        ItemWriter<RawTransaction> writer = transactionWriter != null
                ? transactionWriter
                : stubWriter("transactionWriter");

        return new StepBuilder("transactionStep", jobRepository)
                .<TransactionCsvRow, RawTransaction>chunk(100, transactionManager)
                .reader(reader)
                .processor(transactionProcessor)
                .writer(writer)
                .faultTolerant()
                .skip(ValidationException.class)
                .skipLimit(skipLimit)
                .listener((Object) errorLogWriter)
                .build();
    }

    /**
     * Builds the employee processing step with fault-tolerant skip policy.
     *
     * <p>Requirements 6.1, 6.2, 6.3, 6.5: chunk-oriented step with skip on
     * {@link ValidationException}, skipLimit, and {@link ErrorLogWriter} listener.
     *
     * @return the configured {@link Step}
     */
    @Bean
    public Step employeeStep() {
        ItemReader<EmployeeCsvRow> reader = employeeReader != null
                ? employeeReader
                : stubReader("employeeReader");

        ItemWriter<RawEmployee> writer = employeeWriter != null
                ? employeeWriter
                : stubWriter("employeeWriter");

        return new StepBuilder("employeeStep", jobRepository)
                .<EmployeeCsvRow, RawEmployee>chunk(100, transactionManager)
                .reader(reader)
                .processor(employeeProcessor)
                .writer(writer)
                .faultTolerant()
                .skip(ValidationException.class)
                .skipLimit(skipLimit)
                .listener((Object) errorLogWriter)
                .build();
    }

    /**
     * Casts the raw {@link SkipListener} to a typed one via an unchecked cast.
     * {@link ErrorLogWriter} implements {@code SkipListener<Object, Object>} which
     * handles any item type at runtime, so this cast is safe.
     */
    @SuppressWarnings("unchecked")
    private <I, O> SkipListener<I, O> asTypedSkipListener(SkipListener<Object, Object> listener) {
        return (SkipListener<I, O>) (SkipListener<?, ?>) listener;
    }

    // -----------------------------------------------------------------------
    // Job definitions
    // -----------------------------------------------------------------------

    /**
     * Defines the transaction batch job.
     *
     * <p>Requirement 6.6: job is composed of a single transactionStep.
     *
     * @return the configured {@link Job}
     */
    @Bean
    public Job transactionJob() {
        return new JobBuilder("transactionJob", jobRepository)
                .start(transactionStep())
                .listener(jobExecutionListener)
                .build();
    }

    /**
     * Defines the employee batch job.
     *
     * <p>Requirement 6.6: job is composed of a single employeeStep.
     *
     * @return the configured {@link Job}
     */
    @Bean
    public Job employeeJob() {
        return new JobBuilder("employeeJob", jobRepository)
                .start(employeeStep())
                .listener(jobExecutionListener)
                .build();
    }

    // -----------------------------------------------------------------------
    // Stub helpers (used when Member 1 / Member 3 beans are not yet available)
    // -----------------------------------------------------------------------

    /**
     * Returns a stub {@link ItemReader} that throws {@link UnsupportedOperationException}
     * when called. This allows the application context to start even when the real
     * reader bean (provided by Member 1) is not yet registered.
     *
     * @param name a descriptive name for logging
     * @param <T>  the item type
     * @return a stub reader
     */
    @SuppressWarnings("unchecked")
    private <T> ItemReader<T> stubReader(String name) {
        log.warn("No ItemReader bean found for '{}'. Using stub — job will fail if executed.", name);
        return () -> {
            throw new UnsupportedOperationException(
                    "ItemReader '" + name + "' is not yet implemented. "
                    + "Please provide a bean of the required type.");
        };
    }

    /**
     * Returns a stub {@link ItemWriter} that throws {@link UnsupportedOperationException}
     * when called. This allows the application context to start even when the real
     * writer bean (provided by Member 3) is not yet registered.
     *
     * @param name a descriptive name for logging
     * @param <T>  the item type
     * @return a stub writer
     */
    @SuppressWarnings("unchecked")
    private <T> ItemWriter<T> stubWriter(String name) {
        log.warn("No ItemWriter bean found for '{}'. Using stub — job will fail if executed.", name);
        return chunk -> {
            throw new UnsupportedOperationException(
                    "ItemWriter '" + name + "' is not yet implemented. "
                    + "Please provide a bean of the required type.");
        };
    }
}
