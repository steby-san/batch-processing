package com.example.batch_processing.model.csv;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Input DTO representing a raw row read from an employee CSV/Excel file.
 * Provided by ItemReader (Member 1). All fields are String — parsing and
 * validation are handled by EmployeeProcessor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCsvRow {

    /** Cột employee_id */
    private String employeeId;

    /** Cột full_name */
    private String fullName;

    /** Cột department */
    private String department;

    /** Cột age (chuỗi, chưa parse) */
    private String age;

    /** Cột email */
    private String email;

    /** Cột hire_date (chuỗi, chưa parse) */
    private String hireDate;

    /** Cột salary (chuỗi, chưa parse) */
    private String salary;
}
