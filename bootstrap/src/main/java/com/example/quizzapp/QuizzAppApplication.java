package com.example.quizzapp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.quizzapp")
@EnableJpaRepositories(basePackages = "com.example.quizzapp.infrastructure.persistence")
public class QuizzAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizzAppApplication.class, args);
    }

}