package com.example.batch_processing.batch;

import com.example.batch_processing.model.RawEmployee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class EmployeeItemProcessor implements ItemProcessor<RawEmployee, RawEmployee> {

    @Override
    public RawEmployee process(RawEmployee item) throws Exception {
        if (item.getEmpCode() == null || item.getEmpCode().isEmpty()) {
            log.warn("Skipping employee with empty empCode");
            return null;
        }
        
        item.setCreatedAt(LocalDateTime.now());
        return item;
    }
}
