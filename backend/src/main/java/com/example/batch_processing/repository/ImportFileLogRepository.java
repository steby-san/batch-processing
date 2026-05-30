package com.example.batch_processing.repository;

import com.example.batch_processing.model.ImportFileLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImportFileLogRepository
        extends JpaRepository<ImportFileLog, Long> {

    List<ImportFileLog> findByImportStatus(String status);

    List<ImportFileLog> findTop20ByOrderByStartTimeDesc();

    boolean existsByFileName(String fileName);
}