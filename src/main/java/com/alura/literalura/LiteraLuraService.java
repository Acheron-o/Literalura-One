package com.alura.literalura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Scanner;

/**
 * This is the main service class for LiteraLura.
 * It handles all the business logic and user interaction.
 */
@Service
public class LiteraLuraService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    private Scanner scanner = new Scanner(System.in);

    /**
     * Starts the application and shows the main menu.
     */
    public void start() {
        System.out.println("🎉 Welcome to LiteraLura - Your Personal Library Manager!");
        
        boolean running = true;
        while (running) {
            showMenu();
            int choice = getChoice();
            running = handleChoice(choice);
        }
        
        System.out.println("👋 Thanks for using LiteraLura. Goodbye!");
        scanner.close();
    }

    /**
     * Shows the main menu options.
     */
    private void showMenu() {
        System.out.println("\n📚 LITERALURA - MAIN MENU");
        System.out.println("═".repeat(40));
        System.out.println("1. Add a new book");
        System.out.println("2. Add a new author");
        System.out.println("3. List all books");
        System.out.println("4. List all authors");
        System.out.println("5. Find books by language");
        System.out.println("6. Find authors alive in a specific year");
        System.out.println("7. Search books by title");
        System.out.println("0. Exit");
        System.out.println("═".repeat(40));
        System.out.print("Choose an option (0-7): ");
    }

    /**
     * Gets the user's menu choice.
     */
    private int getChoice() {
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice >= 0 && choice <= 7) {
                return choice;
            } else {
                System.out.println("❌ Please enter a number between 0 and 7");
                return getChoice();
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number");
            return getChoice();
        }
    }

    /**
     * Handles the user's menu choice.
     */
    private boolean handleChoice(int choice) {
        System.out.println();
        switch (choice) {
            case 1:
                addBook();
                return true;
            case 2:
                addAuthor();
                return true;
            case 3:
                listAllBooks();
                return true;
            case 4:
                listAllAuthors();
                return true;
            case 5:
                findBooksByLanguage();
                return true;
            case 6:
                findAuthorsAliveInYear();
                return true;
            case 7:
                searchBooksByTitle();
                return true;
            case 0:
                return false;
            default:
                System.out.println("❌ Invalid option");
                return true;
        }
    }

    /**
     * Adds a new book to the library.
     */
    private void addBook() {
        System.out.println("📖 ADD A NEW BOOK");
        System.out.println("═".repeat(30));
        
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        
        if (title.trim().isEmpty()) {
            System.out.println("❌ Book title cannot be empty");
            return;
        }

        System.out.print("Enter language (e.g., English, Spanish, etc.): ");
        String language = scanner.nextLine();

        System.out.print("Enter publication year (optional): ");
        String yearStr = scanner.nextLine();
        Integer year = null;
        if (!yearStr.trim().isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid year, skipping");
            }
        }

        // Show available authors
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            System.out.println("❌ No authors available. Please add an author first.");
            return;
        }

        System.out.println("\nAvailable authors:");
        for (int i = 0; i < authors.size(); i++) {
            System.out.println((i + 1) + ". " + authors.get(i).getName());
        }

        System.out.print("Choose an author (1-" + authors.size() + "): ");
        try {
            int authorChoice = Integer.parseInt(scanner.nextLine());
            if (authorChoice >= 1 && authorChoice <= authors.size()) {
                Author selectedAuthor = authors.get(authorChoice - 1);
                
                Book book = new Book(title);
                book.setLanguage(language);
                book.setPublicationYear(year);
                book.setAuthor(selectedAuthor);
                
                bookRepository.save(book);
                System.out.println("✅ Book added successfully!");
            } else {
                System.out.println("❌ Invalid author choice");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number");
        }
    }

    /**
     * Adds a new author to the library.
     */
    private void addAuthor() {
        System.out.println("✍️ ADD A NEW AUTHOR");
        System.out.println("═".repeat(30));
        
        System.out.print("Enter author name: ");
        String name = scanner.nextLine();
        
        if (name.trim().isEmpty()) {
            System.out.println("❌ Author name cannot be empty");
            return;
        }

        System.out.print("Enter birth year (optional): ");
        String birthYearStr = scanner.nextLine();
        Integer birthYear = null;
        if (!birthYearStr.trim().isEmpty()) {
            try {
                birthYear = Integer.parseInt(birthYearStr);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid birth year, skipping");
            }
        }

        System.out.print("Enter death year (optional, leave empty if still alive): ");
        String deathYearStr = scanner.nextLine();
        Integer deathYear = null;
        if (!deathYearStr.trim().isEmpty()) {
            try {
                deathYear = Integer.parseInt(deathYearStr);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid death year, skipping");
            }
        }

        Author author = new Author(name);
        author.setBirthYear(birthYear);
        author.setDeathYear(deathYear);
        
        authorRepository.save(author);
        System.out.println("✅ Author added successfully!");
    }

    /**
     * Lists all books in the library.
     */
    private void listAllBooks() {
        System.out.println("📚 ALL BOOKS");
        System.out.println("═".repeat(30));
        
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            System.out.println("📭 No books found in the library.");
        } else {
            System.out.println("Found " + books.size() + " books:\n");
            for (Book book : books) {
                System.out.println("- " + book);
            }
        }
    }

    /**
     * Lists all authors in the library.
     */
    private void listAllAuthors() {
        System.out.println("👥 ALL AUTHORS");
        System.out.println("═".repeat(30));
        
        List<Author> authors = authorRepository.findAll();
        if (authors.isEmpty()) {
            System.out.println("📭 No authors found in the library.");
        } else {
            System.out.println("Found " + authors.size() + " authors:\n");
            for (Author author : authors) {
                System.out.println("- " + author);
            }
        }
    }

    /**
     * Finds books by language.
     */
    private void findBooksByLanguage() {
        System.out.println("🌍 FIND BOOKS BY LANGUAGE");
        System.out.println("═".repeat(30));
        
        System.out.print("Enter language: ");
        String language = scanner.nextLine();
        
        if (language.trim().isEmpty()) {
            System.out.println("❌ Language cannot be empty");
            return;
        }

        List<Book> books = bookRepository.findByLanguageIgnoreCase(language);
        if (books.isEmpty()) {
            System.out.println("📭 No books found in language: " + language);
        } else {
            System.out.println("Found " + books.size() + " books in " + language + ":\n");
            for (Book book : books) {
                System.out.println("- " + book);
            }
        }
    }

    /**
     * Finds authors alive in a specific year.
     */
    private void findAuthorsAliveInYear() {
        System.out.println("📅 FIND AUTHORS ALIVE IN YEAR");
        System.out.println("═".repeat(30));
        
        System.out.print("Enter year: ");
        String yearStr = scanner.nextLine();
        
        try {
            int year = Integer.parseInt(yearStr);
            List<Author> authors = authorRepository.findAuthorsAliveInYear(year);
            
            if (authors.isEmpty()) {
                System.out.println("📭 No authors found alive in year " + year);
            } else {
                System.out.println("Found " + authors.size() + " authors alive in " + year + ":\n");
                for (Author author : authors) {
                    System.out.println("- " + author);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid year");
        }
    }

    /**
     * Searches books by title.
     */
    private void searchBooksByTitle() {
        System.out.println("🔍 SEARCH BOOKS BY TITLE");
        System.out.println("═".repeat(30));
        
        System.out.print("Enter title to search: ");
        String title = scanner.nextLine();
        
        if (title.trim().isEmpty()) {
            System.out.println("❌ Search term cannot be empty");
            return;
        }

        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        if (books.isEmpty()) {
            System.out.println("📭 No books found with title containing: " + title);
        } else {
            System.out.println("Found " + books.size() + " books matching '" + title + "':\n");
            for (Book book : books) {
                System.out.println("- " + book);
            }
        }
    }
}
