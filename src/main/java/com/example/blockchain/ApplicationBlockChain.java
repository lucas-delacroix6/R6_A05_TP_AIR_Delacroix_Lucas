package com.example.blockchain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ApplicationBlockChain {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationBlockChain.class, args);
        System.out.println("Backend Blockchain démarré sur http://localhost:8080");
    }
}