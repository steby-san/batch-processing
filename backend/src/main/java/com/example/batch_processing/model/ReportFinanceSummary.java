package com.example.batch_processing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "batch_processing_db.report_finance_summary")
public class ReportFinanceSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REPORT_MONTH")
    private LocalDate reportMonth;

    @Column(name = "TOTAL_REVENUE")
    private BigDecimal totalRevenue;

    @Column(name = "TOTAL_TRANSACTION")
    private Long totalTransaction;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

}
