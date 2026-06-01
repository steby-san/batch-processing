package com.example.batch_processing.dto.reponse;

import java.util.List;
import java.util.Map;

public class ReportResponse {

    private String domain;

    private List<String> columns;

    private List<Map<String, Object>> data;

    public ReportResponse() {
    }

    public ReportResponse(
            String domain,
            List<String> columns,
            List<Map<String, Object>> data) {

        this.domain = domain;
        this.columns = columns;
        this.data = data;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}