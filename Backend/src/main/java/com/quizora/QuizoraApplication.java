package com.quizora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
// @EnableConfigurationProperties
public class QuizoraApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuizoraApplication.class, args);
    }
}
