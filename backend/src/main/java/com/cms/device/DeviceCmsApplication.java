package com.cms.device;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeviceCmsApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(DeviceCmsApplication.class, args);
    }
} 