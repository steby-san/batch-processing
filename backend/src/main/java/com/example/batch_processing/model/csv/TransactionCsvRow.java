package com.example.batch_processing.model.csv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input DTO representing a raw row read from a transaction CSV/Excel file.
 * Provided by ItemReader (Member 1). All fields are String — parsing and
 * validation are handled by TransactionProcessor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCsvRow {

    /** Cột transaction_id */
    private String transactionId;

    /** Cột account_id */
    private String accountId;

    /** Cột amount (chuỗi, chưa parse) */
    private String amount;

    /** Cột currency */
    private String currency;

    /** Cột transaction_date (chuỗi, chưa parse) */
    private String transactionDate;

    /** Cột description (tùy chọn) */
    private String description;
}
