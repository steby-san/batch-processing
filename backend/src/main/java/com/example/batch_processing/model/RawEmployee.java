package com.example.batch_processing.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "batch_processing_db.raw_employee")
public class RawEmployee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EMP_CODE")
    private String empCode;

    @Column(name = "FULL_NAME")
    private String fullName;

    @Column(name = "DEPARTMENT")
    private String department;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

}
