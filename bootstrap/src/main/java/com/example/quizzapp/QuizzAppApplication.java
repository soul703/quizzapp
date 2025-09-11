package com.example.quizzapp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.quizzapp")
public class QuizzAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizzAppApplication.class, args);
    }

}