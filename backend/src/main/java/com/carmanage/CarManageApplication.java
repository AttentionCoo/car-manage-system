package com.carmanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.carmanage.mapper")
public class CarManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarManageApplication.class, args);
    }
}
