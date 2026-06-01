package com.example.batch_processing.service;

import java.util.List;

public interface S3Service {

    List<String> getPendingFiles();

    void moveFile(
            String sourceKey,
            String destinationKey
    );
}