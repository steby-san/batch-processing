package com.example.batch_processing.service;

import com.example.batch_processing.repository.RawEmployeeRepository;
import com.example.batch_processing.repository.RawTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}