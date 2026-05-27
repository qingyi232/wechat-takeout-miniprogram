package com.koala.takeout;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@MapperScan("com.koala.takeout.mapper")
public class KoalaTakeoutApplication {
    public static void main(String[] args) {
        SpringApplication.run(KoalaTakeoutApplication.class, args);
    }
}
