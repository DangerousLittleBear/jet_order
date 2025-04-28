package com.example.ordersystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "paymentExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();

        executor.setCorePoolSize(processors * 4);
        executor.setMaxPoolSize(processors * 8);
        executor.setQueueCapacity(10000);      
        executor.setThreadNamePrefix("AsyncExecutor-");
        executor.initialize();

        return executor;
    }

    @Bean(name = "dbSyncExecutor")
    public Executor getDbSyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();

        executor.setCorePoolSize(processors * 4);
        executor.setMaxPoolSize(processors * 8);
        executor.setQueueCapacity(10000);
        executor.setThreadNamePrefix("DbSyncExecutor-");
        executor.initialize();

        return executor;
    }
}
