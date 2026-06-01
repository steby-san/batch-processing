package com.example.batch_processing.config;

import com.example.batch_processing.batch.EmployeeItemProcessor;
import com.example.batch_processing.batch.TransactionItemProcessor;
import com.example.batch_processing.model.RawEmployee;
import com.example.batch_processing.model.RawTransaction;
import com.example.batch_processing.repository.RawEmployeeRepository;
import com.example.batch_processing.repository.RawTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.data.RepositoryItemWriter;
import org.springframework.batch.infrastructure.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchStepConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RawTransactionRepository rawTransactionRepository;
    private final RawEmployeeRepository rawEmployeeRepository;
    private final TransactionItemProcessor transactionItemProcessor;
    private final EmployeeItemProcessor employeeItemProcessor;

    @Bean
    @StepScope
    public FlatFileItemReader<RawTransaction> transactionItemReader(
            @Value("#{jobParameters['filePath']}") String filePath) {
        
        return new FlatFileItemReaderBuilder<RawTransaction>()
                .name("transactionItemReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .delimited()
                .names("txnCode", "amount", "status")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<RawTransaction>() {{
                    setTargetType(RawTransaction.class);
                }})
                .build();
    }

    @Bean
    public RepositoryItemWriter<RawTransaction> transactionItemWriter() {
        return new RepositoryItemWriterBuilder<RawTransaction>()
                .repository(rawTransactionRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step transactionStep() {
        return new StepBuilder("transactionStep", jobRepository)
                .<RawTransaction, RawTransaction>chunk(100, transactionManager)
                .reader(transactionItemReader(null))
                .processor(transactionItemProcessor)
                .writer(transactionItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RawEmployee> employeeItemReader(
            @Value("#{jobParameters['filePath']}") String filePath) {
        
        return new FlatFileItemReaderBuilder<RawEmployee>()
                .name("employeeItemReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .delimited()
                .names("empCode", "fullName", "department")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<RawEmployee>() {{
                    setTargetType(RawEmployee.class);
                }})
                .build();
    }

    @Bean
    public RepositoryItemWriter<RawEmployee> employeeItemWriter() {
        return new RepositoryItemWriterBuilder<RawEmployee>()
                .repository(rawEmployeeRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step employeeStep() {
        return new StepBuilder("employeeStep", jobRepository)
                .<RawEmployee, RawEmployee>chunk(100, transactionManager)
                .reader(employeeItemReader(null))
                .processor(employeeItemProcessor)
                .writer(employeeItemWriter())
                .build();
    }
}
