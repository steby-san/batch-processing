package com.example.batch_processing.service;

import com.example.batch_processing.model.JobExecutionLog;
import com.example.batch_processing.repository.JobExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobExecutionLogService {

    private final JobExecutionLogRepository repository;

    public JobExecutionLog save(
            JobExecutionLog log
    ) {
        return repository.save(log);
    }
}