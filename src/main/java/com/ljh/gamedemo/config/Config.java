package com.ljh.gamedemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 配置类
 *
 * @Author: Heiku
 * @Date: 2019/9/19
 */

@Configuration
@ComponentScan("com.ljh.gamedemo.module.*.event")
@EnableAsync
public class Config {

    @Bean
    public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor(){
        return new SimpleAsyncTaskExecutor();
    }
}
