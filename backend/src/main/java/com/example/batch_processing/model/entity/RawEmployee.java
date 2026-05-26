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
 * JPA Entity ánh xạ tới bảng RAW_EMPLOYEES.
 * Là đầu ra của EmployeeProcessor sau khi validate và ánh xạ từ EmployeeCsvRow.
 */
@Entity
@Table(name = "RAW_EMPLOYEES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawEmployee {

    @Id
    @Column(name = "employee_id", nullable = false, length = 100)
    private String employeeId;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal salary;

    @Column(name = "status", nullable = false, length = 20)
    private String status;  // Luôn là "PENDING" khi ra khỏi Processor
}
