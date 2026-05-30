package com.example.batch_processing.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReportFinanceSummaryRepository extends JpaRepository<ReportFinanceSummary, Long>, JpaSpecificationExecutor<ReportFinanceSummary> {

}