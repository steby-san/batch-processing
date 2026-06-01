package com.example.batch_processing.repository;

import com.example.batch_processing.model.RawEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RawEmployeeRepository
        extends JpaRepository<RawEmployee, Long> {

    List<RawEmployee> findByDepartment(String department);

    long countByDepartment(String department);

    boolean existsByEmpCode(String empCode);

    @org.springframework.data.jpa.repository.Query("""
            SELECT e.department AS department, COUNT(e) AS employeeCount
            FROM RawEmployee e
            GROUP BY e.department
            """)
    List<com.example.batch_processing.projection.HrReportProjection> getHrReport();
}