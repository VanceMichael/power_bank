package com.powerbank;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.powerbank.mapper")
public class PowerBankApplication {
    public static void main(String[] args) {
        SpringApplication.run(PowerBankApplication.class, args);
    }
}
