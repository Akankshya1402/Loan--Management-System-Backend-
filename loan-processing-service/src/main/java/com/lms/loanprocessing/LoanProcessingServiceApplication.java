package com.lms.loanprocessing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableFeignClients(basePackages = "com.lms.loanprocessing.client")
public class LoanProcessingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoanProcessingServiceApplication.class, args);
    }
}

