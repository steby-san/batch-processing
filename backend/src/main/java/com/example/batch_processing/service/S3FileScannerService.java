package com.example.batch_processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileScannerService {

    private final S3Service s3Service;

    private final FileDetectionService fileDetectionService;

    public int scanAndProcessFiles() {

        int count = 0;

        for (String fileName :
                s3Service.getPendingFiles()) {

            String jobType =
                    fileDetectionService.detectJobType(
                            fileName
                    );

            log.info(
                    "File={} Job={}",
                    fileName,
                    jobType
            );

            s3Service.moveFile(
                    fileName,
                    "processed/" + fileName
            );

            count++;
        }

        return count;
    }
}