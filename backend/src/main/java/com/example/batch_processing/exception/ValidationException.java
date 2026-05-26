package com.example.batch_processing.exception;

/**
 * Custom unchecked exception thrown when a data row fails validation
 * in TransactionProcessor or EmployeeProcessor.
 *
 * <p>Using RuntimeException allows Spring Batch's faultTolerant().skip()
 * to handle it without requiring a checked throws declaration on
 * ItemProcessor.process().</p>
 *
 * <p>Validates: Requirements 3.7, 3.8, 4.1, 4.2</p>
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
