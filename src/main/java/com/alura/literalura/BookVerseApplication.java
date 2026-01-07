package com.alura.literalura;

import com.alura.literalura.ui.ConsoleInterface;
import com.alura.literalura.service.BookManagementService;
import com.alura.literalura.service.AuthorManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the BookVerse application.
 * An interactive book management system with external API integration.
 */
@SpringBootApplication
public class BookVerseApplication implements CommandLineRunner {

    private final BookManagementService bookService;
    private final AuthorManagementService authorService;

    @Autowired
    public BookVerseApplication(BookManagementService bookService, 
                               AuthorManagementService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookVerseApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ConsoleInterface consoleInterface = new ConsoleInterface(bookService, authorService);
        consoleInterface.start();
    }
}
