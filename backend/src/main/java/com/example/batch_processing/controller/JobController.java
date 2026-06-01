package com.example.batch_processing.controller;

import com.example.batch_processing.model.JobExecutionLog;
import com.example.batch_processing.scheduler.JobScheduler;
import com.example.batch_processing.service.JobExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobScheduler jobScheduler;
    private final JobExecutionLogService jobExecutionLogService;

    @PostMapping("/trigger")
    public ResponseEntity<?> triggerJobManually() {
        new Thread(() -> {
            try {
                jobScheduler.triggerManually();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        
        return ResponseEntity.ok(Map.of("message", "Job triggered successfully"));
    }

    @GetMapping("/logs")
    public ResponseEntity<List<JobExecutionLog>> getJobLogs() {
        return ResponseEntity.ok(jobExecutionLogService.getAllLogs());
    }
}
