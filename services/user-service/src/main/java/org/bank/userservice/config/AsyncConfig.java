package org.bank.userservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
public class AsyncConfig {

    @Bean(name = "applicationTaskExecutor")
    public Executor taskExecutor() {

        log.info("Initializing application task executor");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("user-delete-");

        log.debug(
                "Task executor configured: corePoolSize=4, maxPoolSize=8, queueCapacity=50, threadNamePrefix=user-delete-"
        );

        executor.initialize();

        log.info("Application task executor initialized successfully");

        return executor;
    }
}