package com.example.batch_processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public List<String> getPendingFiles() {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            
            return response.contents().stream()
                    .map(S3Object::key)
                    .filter(k -> !k.startsWith("processed/"))
                    .collect(Collectors.toList());
        } catch (S3Exception e) {
            log.error("Error listing files from S3: {}", e.awsErrorDetails().errorMessage());
            return List.of(); // Return empty list on error
        }
    }

    @Override
    public void moveFile(String sourceKey, String destinationKey) {
        try {
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(destinationKey)
                    .build();
            s3Client.copyObject(copyObjectRequest);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(sourceKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            
            log.info("Moved file {} to {}", sourceKey, destinationKey);
        } catch (S3Exception e) {
            log.error("Error moving file in S3: {}", e.awsErrorDetails().errorMessage());
        }
    }

    @Override
    public File downloadFile(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
            byte[] data = objectBytes.asByteArray();

            File tempFile = Files.createTempFile("batch-", "-" + key.replaceAll("/", "_")).toFile();
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(data);
            }
            return tempFile;
        } catch (S3Exception | IOException e) {
            log.error("Error downloading file {}: {}", key, e.getMessage());
            throw new RuntimeException("Could not download file from S3", e);
        }
    }
}