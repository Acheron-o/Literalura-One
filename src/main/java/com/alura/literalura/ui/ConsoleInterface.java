package com.alura.literalura.ui;

import com.alura.literalura.model.Book;
import com.alura.literalura.model.Author;
import com.alura.literalura.service.BookManagementService;
import com.alura.literalura.service.AuthorManagementService;
import com.alura.literalura.service.ExternalBookSearchService;
import com.alura.literalura.exception.BookVerseException;

import java.util.List;
import java.util.Scanner;
import java.util.Optional;

/**
 * Console-based user interface for the BookVerse application.
 * Provides interactive menu-driven functionality for book and author management.
 */
public class ConsoleInterface {
    
    private final Scanner scanner;
    private final BookManagementService bookService;
    private final AuthorManagementService authorService;
    private final ExternalBookSearchService searchService;
    
    private static final String MENU_HEADER = """
        
        📚 BOOKVERSE - INTERACTIVE LIBRARY MANAGEMENT SYSTEM
        ═══════════════════════════════════════════════════════════
        
        Select an option by entering the corresponding number:
        """;
    
    private static final String MENU_OPTIONS = """
        1️⃣  Search for books online
        2️⃣  Display all registered books
        3️⃣  Display all registered authors
        4️⃣  Find authors alive in a specific year
        5️⃣  Filter books by language
        6️⃣  Search books by author name
        7️⃣  Display statistics
        8️⃣  Advanced book search
        9️⃣  Manage authors
        🔟  Export data
        0️⃣  Exit application
        
        ═══════════════════════════════════════════════════════════
        """;

    public ConsoleInterface(BookManagementService bookService, 
                           AuthorManagementService authorService) {
        this.scanner = new Scanner(System.in);
        this.bookService = bookService;
        this.authorService = authorService;
        this.searchService = new ExternalBookSearchService(bookService, authorService);
    }

    public void start() {
        System.out.println("🎉 Welcome to BookVerse - Your Personal Library Manager!");
        System.out.println("Initializing system components...");
        
        boolean running = true;
        
        while (running) {
            try {
                displayMenu();
                int choice = getUserChoice();
                running = processChoice(choice);
            } catch (Exception e) {
                System.err.println("❌ An unexpected error occurred: " + e.getMessage());
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
        
        System.out.println("👋 Thank you for using BookVerse. Goodbye!");
        scanner.close();
    }

    private void displayMenu() {
        System.out.println(MENU_HEADER);
        System.out.println(MENU_OPTIONS);
        System.out.print("👉 Your choice: ");
    }

    private int getUserChoice() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                if (choice >= 0 && choice <= 10) {
                    return choice;
                } else {
                    System.out.print("❌ Please enter a number between 0 and 10: ");
                }
            } catch (NumberFormatException e) {
                System.out.print("❌ Please enter a valid number: ");
            }
        }
    }

    private boolean processChoice(int choice) {
        System.out.println();
        
        return switch (choice) {
            case 1 -> { searchBooksOnline(); yield true; }
            case 2 -> { displayAllBooks(); yield true; }
            case 3 -> { displayAllAuthors(); yield true; }
            case 4 -> { findAuthorsByYear(); yield true; }
            case 5 -> { filterBooksByLanguage(); yield true; }
            case 6 -> { searchBooksByAuthor(); yield true; }
            case 7 -> { displayStatistics(); yield true; }
            case 8 -> { advancedBookSearch(); yield true; }
            case 9 -> { manageAuthors(); yield true; }
            case 10 -> { exportData(); yield true; }
            case 0 -> false;
            default -> {
                System.out.println("❌ Invalid option selected.");
                yield true;
            }
        };
    }

    private void searchBooksOnline() {
        System.out.println("🔍 ONLINE BOOK SEARCH");
        System.out.println("═".repeat(50));
        System.out.print("Enter book title or keywords: ");
        String query = scanner.nextLine().trim();
        
        if (query.isEmpty()) {
            System.out.println("❌ Search query cannot be empty.");
            return;
        }
        
        try {
            Optional<Book> result = searchService.searchAndSaveBook(query);
            if (result.isPresent()) {
                System.out.println("✅ Book successfully found and saved!");
                System.out.println(result.get());
            } else {
                System.out.println("❌ No books found matching your search criteria.");
            }
        } catch (BookVerseException e) {
            System.err.println("❌ Search failed: " + e.getMessage());
        }
    }

    private void displayAllBooks() {
        System.out.println("📚 ALL REGISTERED BOOKS");
        System.out.println("═".repeat(50));
        
        List<Book> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("📭 No books registered in the system yet.");
            return;
        }
        
        System.out.printf("Found %d books in the library:%n%n", books.size());
        books.forEach(book -> {
            System.out.println(book);
            System.out.println();
        });
    }

    private void displayAllAuthors() {
        System.out.println("👥 ALL REGISTERED AUTHORS");
        System.out.println("═".repeat(50));
        
        List<Author> authors = authorService.getAllAuthors();
        if (authors.isEmpty()) {
            System.out.println("📭 No authors registered in the system yet.");
            return;
        }
        
        System.out.printf("Found %d authors in the library:%n%n", authors.size());
        authors.forEach(author -> {
            System.out.println(author);
            System.out.println();
        });
    }

    private void findAuthorsByYear() {
        System.out.println("📅 AUTHORS BY YEAR");
        System.out.println("═".repeat(50));
        System.out.print("Enter the year to search for living authors: ");
        
        try {
            int year = Integer.parseInt(scanner.nextLine().trim());
            List<Author> authors = authorService.findAuthorsAliveInYear(year);
            
            if (authors.isEmpty()) {
                System.out.printf("📭 No authors found alive in year %d.%n", year);
            } else {
                System.out.printf("Found %d authors alive in year %d:%n%n", authors.size(), year);
                authors.forEach(author -> {
                    System.out.println(author);
                    System.out.println();
                });
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid year number.");
        }
    }

    private void filterBooksByLanguage() {
        System.out.println("🌍 BOOKS BY LANGUAGE");
        System.out.println("═".repeat(50));
        System.out.println("Available language codes:");
        System.out.println("  en - English");
        System.out.println("  es - Spanish");
        System.out.println("  fr - French");
        System.out.println("  pt - Portuguese");
        System.out.println("  de - German");
        System.out.println("  it - Italian");
        System.out.println();
        System.out.print("Enter language code: ");
        
        String language = scanner.nextLine().trim().toLowerCase();
        List<Book> books = bookService.findBooksByLanguage(language);
        
        if (books.isEmpty()) {
            System.out.printf("📭 No books found in language '%s'.%n", language);
        } else {
            System.out.printf("Found %d books in language '%s':%n%n", books.size(), language);
            books.forEach(book -> {
                System.out.println(book);
                System.out.println();
            });
        }
    }

    private void searchBooksByAuthor() {
        System.out.println("🔍 BOOKS BY AUTHOR");
        System.out.println("═".repeat(50));
        System.out.print("Enter author name: ");
        String authorName = scanner.nextLine().trim();
        
        if (authorName.isEmpty()) {
            System.out.println("❌ Author name cannot be empty.");
            return;
        }
        
        List<Book> books = bookService.findBooksByAuthor(authorName);
        if (books.isEmpty()) {
            System.out.printf("📭 No books found for author '%s'.%n", authorName);
        } else {
            System.out.printf("Found %d books by '%s':%n%n", books.size(), authorName);
            books.forEach(book -> {
                System.out.println(book);
                System.out.println();
            });
        }
    }

    private void displayStatistics() {
        System.out.println("📊 LIBRARY STATISTICS");
        System.out.println("═".repeat(50));
        
        var stats = bookService.getLibraryStatistics();
        System.out.printf("Total Books: %d%n", stats.totalBooks());
        System.out.printf("Total Authors: %d%n", stats.totalAuthors());
        System.out.printf("Most Common Language: %s%n", stats.mostCommonLanguage());
        System.out.printf("Author with Most Books: %s (%d books)%n", 
                         stats.mostProlificAuthor(), stats.mostProlificAuthorBookCount());
        System.out.printf("Average Downloads per Book: %.1f%n", stats.averageDownloads());
    }

    private void advancedBookSearch() {
        System.out.println("🔬 ADVANCED BOOK SEARCH");
        System.out.println("═".repeat(50));
        System.out.println("This feature is coming soon!");
        System.out.println("Will include filters for publication year, download count, etc.");
    }

    private void manageAuthors() {
        System.out.println("👤 AUTHOR MANAGEMENT");
        System.out.println("═".repeat(50));
        System.out.println("This feature is coming soon!");
        System.out.println("Will include add, edit, delete author functionality.");
    }

    private void exportData() {
        System.out.println("💾 DATA EXPORT");
        System.out.println("═".repeat(50));
        System.out.println("This feature is coming soon!");
        System.out.println("Will include export to CSV, JSON, and other formats.");
    }
}
