package com.example.batch_processing.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ImportFileLogRepository extends JpaRepository<ImportFileLog, Long>, JpaSpecificationExecutor<ImportFileLog> {

}