package com.example.batch_processing.processor;

import com.example.batch_processing.exception.ValidationException;
import com.example.batch_processing.model.csv.TransactionCsvRow;
import com.example.batch_processing.model.entity.RawTransaction;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Spring Batch ItemProcessor that validates and maps TransactionCsvRow to RawTransaction.
 *
 * <p>Validation order:
 * <ol>
 *   <li>transaction_id — required (not null/blank)</li>
 *   <li>account_id — required (not null/blank)</li>
 *   <li>amount — required, parseable as BigDecimal, in range [0, 999999999.99]</li>
 *   <li>currency — required, matches ^[A-Za-z]{3}$</li>
 *   <li>transaction_date — required, parseable as yyyy-MM-dd</li>
 * </ol>
 *
 * <p>Validates: Requirements 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9, 3.1, 3.2, 3.3, 3.7
 */
@Component
public class TransactionProcessor implements ItemProcessor<TransactionCsvRow, RawTransaction> {

    private static final BigDecimal AMOUNT_MIN = BigDecimal.ZERO;
    private static final BigDecimal AMOUNT_MAX = new BigDecimal("999999999.99");
    private static final Pattern CURRENCY_PATTERN = Pattern.compile("^[A-Za-z]{3}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public RawTransaction process(TransactionCsvRow item) {
        // 1. Validate transaction_id
        if (isBlank(item.getTransactionId())) {
            throw new ValidationException("transaction_id is required");
        }

        // 2. Validate account_id
        if (isBlank(item.getAccountId())) {
            throw new ValidationException("account_id is required");
        }

        // 3. Validate amount — null/blank check
        if (isBlank(item.getAmount())) {
            throw new ValidationException("amount is required");
        }

        // 3. Validate amount — parse
        BigDecimal amount;
        try {
            amount = new BigDecimal(item.getAmount().trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("amount is not a valid number", e);
        }

        // 3. Validate amount — range [0, 999999999.99]
        if (amount.compareTo(AMOUNT_MIN) < 0) {
            throw new ValidationException("amount must be >= 0");
        }
        if (amount.compareTo(AMOUNT_MAX) > 0) {
            throw new ValidationException("amount must be <= 999999999.99");
        }

        // 4. Validate currency — null/blank check
        if (isBlank(item.getCurrency())) {
            throw new ValidationException("currency is required");
        }

        // 4. Validate currency — pattern ^[A-Za-z]{3}$
        if (!CURRENCY_PATTERN.matcher(item.getCurrency().trim()).matches()) {
            throw new ValidationException("currency must be a 3-letter alphabetic code");
        }

        // 5. Validate transaction_date — null/blank check
        if (isBlank(item.getTransactionDate())) {
            throw new ValidationException("transaction_date is required");
        }

        // 5. Validate transaction_date — parse yyyy-MM-dd
        LocalDate transactionDate;
        try {
            transactionDate = LocalDate.parse(item.getTransactionDate().trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException("transaction_date must be in yyyy-MM-dd format", e);
        }

        // Map to RawTransaction with status = "PENDING"
        String description = isBlank(item.getDescription()) ? null : item.getDescription();

        return RawTransaction.builder()
                .transactionId(item.getTransactionId().trim())
                .accountId(item.getAccountId().trim())
                .amount(amount)
                .currency(item.getCurrency().trim())
                .transactionDate(transactionDate)
                .description(description)
                .status("PENDING")
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
