package com.example.batch_processing.scheduler;

import com.example.batch_processing.service.S3FileScannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {

    private final S3FileScannerService s3FileScannerService;

    /**
     * Ngăn Job chạy chồng nhau
     */
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Value("${scheduler.enabled:true}")
    private boolean schedulerEnabled;

    /**
     * Chạy theo cron trong application.yml
     *
     * scheduler:
     *   cron: 0 0 1 * * ?
     */
    @Scheduled(cron = "${scheduler.cron}")
    public void scanAndProcessFiles() {

        if (!schedulerEnabled) {
            log.info("Scheduler is disabled.");
            return;
        }

        if (!running.compareAndSet(false, true)) {
            log.warn("Previous batch job is still running. Skip this execution.");
            return;
        }

        Instant startTime = Instant.now();

        log.info("==================================================");
        log.info("BATCH JOB STARTED");
        log.info("Start Time: {}", startTime);
        log.info("==================================================");

        try {

            int processedFiles =
                    s3FileScannerService.scanAndProcessFiles();

            log.info("Batch processing completed successfully.");
            log.info("Total processed files: {}", processedFiles);

        } catch (Exception ex) {

            log.error(
                    "Batch processing failed: {}",
                    ex.getMessage(),
                    ex
            );

        } finally {

            Instant endTime = Instant.now();

            long duration =
                    Duration.between(startTime, endTime).toSeconds();

            log.info("==================================================");
            log.info("BATCH JOB FINISHED");
            log.info("End Time: {}", endTime);
            log.info("Duration: {} seconds", duration);
            log.info("==================================================");

            running.set(false);
        }
    }

    /**
     * Cho phép gọi từ API để chạy thủ công
     */
    public void triggerManually() {

        log.info("Manual batch execution requested.");

        scanAndProcessFiles();
    }
}