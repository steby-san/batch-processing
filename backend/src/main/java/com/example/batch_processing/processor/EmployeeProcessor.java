package com.example.batch_processing.processor;

import com.example.batch_processing.exception.ValidationException;
import com.example.batch_processing.model.csv.EmployeeCsvRow;
import com.example.batch_processing.model.entity.RawEmployee;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;

/**
 * Spring Batch ItemProcessor that validates and maps {@link EmployeeCsvRow} to {@link RawEmployee}.
 *
 * <p>Validation order: employee_id → full_name → department → age → email → hire_date → salary</p>
 * <p>Any validation failure throws {@link ValidationException}. Technical exceptions
 * (NumberFormatException, DateTimeParseException) are wrapped into ValidationException.</p>
 *
 * <p>Validates: Requirements 2.1–2.13, 3.4, 3.5, 3.6, 3.8</p>
 */
@Component
public class EmployeeProcessor implements ItemProcessor<EmployeeCsvRow, RawEmployee> {

    private static final Set<String> VALID_DEPARTMENTS = Set.of(
            "HR", "IT", "FINANCE", "OPERATIONS", "MARKETING", "SALES"
    );

    private static final String EMAIL_PATTERN = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public RawEmployee process(EmployeeCsvRow item) throws ValidationException {
        // 1. Validate employee_id
        if (item.getEmployeeId() == null || item.getEmployeeId().isBlank()) {
            throw new ValidationException("employee_id is required");
        }

        // 2. Validate full_name
        if (item.getFullName() == null || item.getFullName().isBlank()) {
            throw new ValidationException("full_name is required");
        }

        // 3. Validate department
        if (item.getDepartment() == null || item.getDepartment().isBlank()) {
            throw new ValidationException("department is required");
        }
        if (!VALID_DEPARTMENTS.contains(item.getDepartment())) {
            throw new ValidationException("department must be one of: HR, IT, FINANCE, OPERATIONS, MARKETING, SALES");
        }

        // 4. Validate age
        if (item.getAge() == null || item.getAge().isBlank()) {
            throw new ValidationException("age is required");
        }
        int age;
        try {
            age = Integer.parseInt(item.getAge());
        } catch (NumberFormatException e) {
            throw new ValidationException("age is not a valid integer", e);
        }
        if (age < 18) {
            throw new ValidationException("age must be >= 18");
        }
        if (age > 65) {
            throw new ValidationException("age must be <= 65");
        }

        // 5. Validate email
        if (item.getEmail() == null || item.getEmail().isBlank()) {
            throw new ValidationException("email is required");
        }
        if (!item.getEmail().matches(EMAIL_PATTERN)) {
            throw new ValidationException("email format is invalid");
        }

        // 6. Validate hire_date
        if (item.getHireDate() == null || item.getHireDate().isBlank()) {
            throw new ValidationException("hire_date is required");
        }
        LocalDate hireDate;
        try {
            hireDate = LocalDate.parse(item.getHireDate(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ValidationException("hire_date must be in yyyy-MM-dd format", e);
        }

        // 7. Validate salary
        if (item.getSalary() == null || item.getSalary().isBlank()) {
            throw new ValidationException("salary is required");
        }
        BigDecimal salary;
        try {
            salary = new BigDecimal(item.getSalary());
        } catch (NumberFormatException e) {
            throw new ValidationException("salary is not a valid number", e);
        }
        if (salary.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("salary must be >= 0");
        }

        // Map to RawEmployee with status = "PENDING"
        return RawEmployee.builder()
                .employeeId(item.getEmployeeId())
                .fullName(item.getFullName())
                .department(item.getDepartment())
                .age(age)
                .email(item.getEmail())
                .hireDate(hireDate)
                .salary(salary)
                .status("PENDING")
                .build();
    }
}
