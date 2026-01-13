package com.alura.literalura;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is the main class for the LiteraLura application.
 * It's a simple book management system that helps you keep track of books.
 */
@SpringBootApplication
public class LiteraLuraApplication implements CommandLineRunner {

    @Autowired
    private LiteraLuraService service;

    public static void main(String[] args) {
        SpringApplication.run(LiteraLuraApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        service.start();
    }
}
