package com.example.batch_processing.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class S3ServiceImpl implements S3Service {

    @Override
    public List<String> getPendingFiles() {

        return List.of(
                "employee.csv",
                "transaction.csv"
        );
    }

    @Override
    public void moveFile(
            String sourceKey,
            String destinationKey
    ) {

        System.out.println(
                sourceKey + " -> " + destinationKey
        );
    }
}