package com.example.batch_processing.controller;

import com.example.batch_processing.dto.reponse.ReportResponse;
import com.example.batch_processing.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/finance")
    public ResponseEntity<ReportResponse> getFinanceReport() {
        return ResponseEntity.ok(reportService.getFinanceReport());
    }

    @GetMapping("/hr")
    public ResponseEntity<ReportResponse> getHrReport() {
        return ResponseEntity.ok(reportService.getHrReport());
    }
}
