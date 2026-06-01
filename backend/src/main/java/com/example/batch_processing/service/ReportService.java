package com.example.batch_processing.service;

import com.example.batch_processing.dto.reponse.ReportResponse;
import com.example.batch_processing.projection.FinanceReportProjection;
import com.example.batch_processing.projection.HrReportProjection;
import com.example.batch_processing.repository.RawEmployeeRepository;
import com.example.batch_processing.repository.RawTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RawTransactionRepository transactionRepository;
    private final RawEmployeeRepository employeeRepository;

    public long totalTransactions() {
        return transactionRepository.count();
    }

    public long totalEmployees() {
        return employeeRepository.count();
    }

    public ReportResponse getFinanceReport() {
        List<FinanceReportProjection> projections = transactionRepository.getFinanceReport();
        List<Map<String, Object>> data = new ArrayList<>();
        
        for (FinanceReportProjection proj : projections) {
            Map<String, Object> row = new HashMap<>();
            row.put("month", proj.getMonth());
            row.put("revenue", proj.getRevenue());
            row.put("transactionCount", proj.getTransactionCount());
            data.add(row);
        }

        return new ReportResponse("FINANCE", List.of("month", "revenue", "transactionCount"), data);
    }

    public ReportResponse getHrReport() {
        List<HrReportProjection> projections = employeeRepository.getHrReport();
        List<Map<String, Object>> data = new ArrayList<>();
        
        for (HrReportProjection proj : projections) {
            Map<String, Object> row = new HashMap<>();
            row.put("department", proj.getDepartment());
            row.put("employeeCount", proj.getEmployeeCount());
            data.add(row);
        }

        return new ReportResponse("HR", List.of("department", "employeeCount"), data);
    }
}