package com.example.batch_processing.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA Entity ánh xạ tới bảng RAW_TRANSACTIONS.
 * Là đầu ra của TransactionProcessor sau khi validate và ánh xạ từ TransactionCsvRow.
 * Trường status luôn là "PENDING" khi ra khỏi Processor.
 */
@Entity
@Table(name = "RAW_TRANSACTIONS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawTransaction {

    @Id
    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;

    @Column(name = "account_id", nullable = false, length = 100)
    private String accountId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "description", length = 500)
    private String description;  // null nếu CSV rỗng

    @Column(name = "status", nullable = false, length = 20)
    private String status;  // Luôn là "PENDING" khi ra khỏi Processor
}
