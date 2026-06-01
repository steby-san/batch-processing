package com.example.batch_processing.batch;

import com.example.batch_processing.model.RawTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TransactionItemProcessor implements ItemProcessor<RawTransaction, RawTransaction> {

    @Override
    public RawTransaction process(RawTransaction item) throws Exception {
        // Validate or transform data
        if (item.getTxnCode() == null || item.getTxnCode().isEmpty()) {
            log.warn("Skipping transaction with empty txnCode");
            return null; 
        }
        
        item.setCreatedAt(LocalDateTime.now());
        
        // Normalize status
        if (item.getStatus() != null) {
            item.setStatus(item.getStatus().toUpperCase());
        }
        
        return item;
    }
}
