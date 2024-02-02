package com.example.his.api;

import com.example.his.api.async.InitializeWorkAsync;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@SpringBootApplication
@ServletComponentScan
@EnableAsync
@ComponentScan("com.example.*")
@MapperScan("com.example.his.api.db.dao")
@EnableCaching
@EnableScheduling
public class HisApiApplication {
    @Resource
    private InitializeWorkAsync initializeWorkAsync;

    public static void main(String[] args) {
        SpringApplication.run(HisApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        initializeWorkAsync.init();
    }
}
