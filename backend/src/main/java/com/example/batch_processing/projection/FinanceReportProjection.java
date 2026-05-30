package com.example.batch_processing.projection;

import java.math.BigDecimal;

public interface FinanceReportProjection {

    String getMonth();

    BigDecimal getRevenue();

    Long getTransactionCount();
}