package com.example.batch_processing.repository;

import com.example.batch_processing.model.JobExecutionLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface JobExecutionLogRepository
        extends JpaRepository<JobExecutionLog, Long> {

    List<JobExecutionLog> findByStatus(String status);

    List<JobExecutionLog> findByJobName(String jobName);

    List<JobExecutionLog> findByFileName(String fileName);

    List<JobExecutionLog> findTop20ByOrderByStartTimeDesc();

    List<JobExecutionLog> findAllByOrderByStartTimeDesc(Pageable pageable);

    long countByStatus(String status);

    long countByJobName(String jobName);

    @Query("""
            SELECT COUNT(j)
            FROM JobExecutionLog j
            WHERE j.startTime BETWEEN :startTime AND :endTime
            """)
    long countJobsBetween(
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    @Query("""
            SELECT COALESCE(SUM(j.successCount),0)
            FROM JobExecutionLog j
            WHERE j.status='COMPLETED'
            """)
    Long totalSuccessRecords();

    @Query("""
            SELECT COALESCE(SUM(j.failureCount),0)
            FROM JobExecutionLog j
            """)
    Long totalFailedRecords();

    @Query("""
            SELECT j
            FROM JobExecutionLog j
            ORDER BY j.startTime DESC
            LIMIT 1
            """)
    JobExecutionLog getLatestJob();
}