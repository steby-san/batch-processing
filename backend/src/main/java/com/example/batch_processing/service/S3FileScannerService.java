package com.example.batch_processing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.UUID;

@Slf4j
@Service
public class S3FileScannerService {

    private final S3Service s3Service;
    private final FileDetectionService fileDetectionService;
    private final JobLauncher jobLauncher;
    private final Job transactionJob;
    private final Job employeeJob;

    public S3FileScannerService(
            S3Service s3Service,
            FileDetectionService fileDetectionService,
            JobLauncher jobLauncher,
            @Qualifier("transactionJob") Job transactionJob,
            @Qualifier("employeeJob") Job employeeJob) {
        this.s3Service = s3Service;
        this.fileDetectionService = fileDetectionService;
        this.jobLauncher = jobLauncher;
        this.transactionJob = transactionJob;
        this.employeeJob = employeeJob;
    }

    public int scanAndProcessFiles() {

        int count = 0;

        for (String fileName : s3Service.getPendingFiles()) {

            String jobType = fileDetectionService.detectJobType(fileName);

            log.info("File={} Job={}", fileName, jobType);

            if (!"UNKNOWN".equals(jobType)) {
                try {
                    // Download file from S3 to temp dir
                    File tempFile = s3Service.downloadFile(fileName);
                    log.info("Downloaded {} to {}", fileName, tempFile.getAbsolutePath());

                    JobParameters jobParameters = new JobParametersBuilder()
                            .addString("filePath", tempFile.getAbsolutePath())
                            .addString("fileName", fileName)
                            .addString("jobId", UUID.randomUUID().toString()) // To ensure uniqueness
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters();

                    if ("TRANSACTION".equals(jobType)) {
                        jobLauncher.run(transactionJob, jobParameters);
                    } else if ("EMPLOYEE".equals(jobType)) {
                        jobLauncher.run(employeeJob, jobParameters);
                    }

                    // Move to processed after successful launch
                    s3Service.moveFile(fileName, "processed/" + fileName);
                    
                } catch (Exception e) {
                    log.error("Failed to process file {}", fileName, e);
                    // Move to error folder or handle accordingly
                    s3Service.moveFile(fileName, "error/" + fileName);
                }
            } else {
                log.warn("Unknown job type for file {}, skipping...", fileName);
                s3Service.moveFile(fileName, "unknown/" + fileName);
            }

            count++;
        }

        return count;
    }
}