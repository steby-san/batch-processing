package com.example.batch_processing.repository;

import com.example.batch_processing.model.ReportFinanceSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ReportFinanceSummaryRepository
        extends JpaRepository<ReportFinanceSummary, Long> {

    Optional<ReportFinanceSummary>
    findByReportMonth(LocalDate reportMonth);

}