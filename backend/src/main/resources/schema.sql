CREATE DATABASE IF NOT EXISTS batch_processing_db;
USE batch_processing_db;

CREATE TABLE IF NOT EXISTS RAW_TRANSACTION (
                                               ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               TXN_CODE VARCHAR(50),
    AMOUNT DECIMAL(18,2),
    STATUS VARCHAR(20),
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS RAW_EMPLOYEE (
                                            ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            EMP_CODE VARCHAR(50),
    FULL_NAME VARCHAR(255),
    DEPARTMENT VARCHAR(100),
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS REPORT_FINANCE_SUMMARY (
                                                      ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                      REPORT_MONTH DATE,
                                                      TOTAL_REVENUE DECIMAL(18,2),
    TOTAL_TRANSACTION BIGINT,
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS IMPORT_FILE_LOG (
                                               ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               FILE_NAME VARCHAR(255),
    IMPORT_STATUS VARCHAR(20),
    START_TIME DATETIME,
    END_TIME DATETIME,
    ERROR_MESSAGE TEXT
    );
CREATE DATABASE IF NOT EXISTS batch_processing_db;
USE batch_processing_db;

CREATE TABLE IF NOT EXISTS RAW_TRANSACTION (
                                               ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               TXN_CODE VARCHAR(50),
    AMOUNT DECIMAL(18,2),
    STATUS VARCHAR(20),
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS RAW_EMPLOYEE (
                                            ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            EMP_CODE VARCHAR(50),
    FULL_NAME VARCHAR(255),
    DEPARTMENT VARCHAR(100),
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS REPORT_FINANCE_SUMMARY (
                                                      ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                      REPORT_MONTH DATE,
                                                      TOTAL_REVENUE DECIMAL(18,2),
    TOTAL_TRANSACTION BIGINT,
    CREATED_AT DATETIME DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS IMPORT_FILE_LOG (
                                               ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               FILE_NAME VARCHAR(255),
    IMPORT_STATUS VARCHAR(20),
    START_TIME DATETIME,
    END_TIME DATETIME,
    ERROR_MESSAGE TEXT
    );
CREATE TABLE IF NOT EXISTS JOB_EXECUTION_LOG (
                                                 ID BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                 JOB_NAME VARCHAR(100) NOT NULL,
    FILE_NAME VARCHAR(255),
    START_TIME DATETIME,
    END_TIME DATETIME,
    STATUS VARCHAR(30),
    SUCCESS_COUNT BIGINT DEFAULT 0,
    FAILURE_COUNT BIGINT DEFAULT 0,
    ERROR_MESSAGE TEXT
    );