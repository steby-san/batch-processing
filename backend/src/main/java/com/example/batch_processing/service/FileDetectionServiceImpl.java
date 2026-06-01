package com.example.batch_processing.service;

import org.springframework.stereotype.Service;

@Service
public class FileDetectionServiceImpl
        implements FileDetectionService {

    @Override
    public String detectJobType(String fileName) {

        String lower = fileName.toLowerCase();

        if (lower.contains("transaction")) {
            return "TRANSACTION";
        }

        if (lower.contains("employee")) {
            return "EMPLOYEE";
        }

        return "UNKNOWN";
    }
}