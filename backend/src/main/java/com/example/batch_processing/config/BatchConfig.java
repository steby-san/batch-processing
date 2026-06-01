package com.example.batch_processing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final JobRepository jobRepository;

    private final Step transactionStep;
    private final Step employeeStep;

    @Bean
    public Job transactionJob() {

        return new JobBuilder(
                "transactionJob",
                jobRepository
        )
                .start(transactionStep)
                .build();
    }

    @Bean
    public Job employeeJob() {

        return new JobBuilder(
                "employeeJob",
                jobRepository
        )
                .start(employeeStep)
                .build();
    }
}