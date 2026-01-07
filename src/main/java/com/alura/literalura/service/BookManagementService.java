package com.alura.literalura.service;

import com.alura.literalura.model.Book;
import com.alura.literalura.repository.BookRepository;
import com.alura.literalura.exception.BookVerseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service layer for managing book operations.
 * Provides business logic for book CRUD operations and queries.
 */
@Service
@Transactional
public class BookManagementService {
    
    private final BookRepository bookRepository;

    @Autowired
    public BookManagementService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Saves a new book to the database.
     * @param book The book to save
     * @return The saved book with generated ID
     * @throws BookVerseException if book already exists or save fails
     */
    public Book saveBook(Book book) throws BookVerseException {
        try {
            // Check for duplicate by title and author
            Optional<Book> existing = bookRepository.findByTitleAndAuthorId(
                book.getTitle(), 
                book.getAuthor() != null ? book.getAuthor().getId() : null
            );
            
            if (existing.isPresent()) {
                throw new BookVerseException("Book with title '" + book.getTitle() + 
                                           "' already exists for this author.");
            }
            
            return bookRepository.save(book);
        } catch (Exception e) {
            throw new BookVerseException("Failed to save book: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing book.
     * @param id The book ID to update
     * @param bookDetails The updated book details
     * @return The updated book
     * @throws BookVerseException if book not found or update fails
     */
    public Book updateBook(Long id, Book bookDetails) throws BookVerseException {
        Book existingBook = getBookById(id);
        
        existingBook.setTitle(bookDetails.getTitle());
        existingBook.setLanguage(bookDetails.getLanguage());
        existingBook.setDownloadCount(bookDetails.getDownloadCount());
        existingBook.setPublicationYear(bookDetails.getPublicationYear());
        existingBook.setIsbn(bookDetails.getIsbn());
        
        return bookRepository.save(existingBook);
    }

    /**
     * Retrieves a book by its ID.
     * @param id The book ID
     * @return The book if found
     * @throws BookVerseException if book not found
     */
    @Transactional(readOnly = true)
    public Book getBookById(Long id) throws BookVerseException {
        return bookRepository.findById(id)
            .orElseThrow(() -> new BookVerseException("Book with ID " + id + " not found"));
    }

    /**
     * Retrieves all books in the database.
     * @return List of all books
     */
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Searches for books by title.
     * @param title The title to search for (case-insensitive, partial match)
     * @return List of matching books
     */
    @Transactional(readOnly = true)
    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Finds books by language.
     * @param language The language code
     * @return List of books in the specified language
     */
    @Transactional(readOnly = true)
    public List<Book> findBooksByLanguage(String language) {
        return bookRepository.findByLanguage(language.toLowerCase());
    }

    /**
     * Finds books by author name.
     * @param authorName The author name to search for
     * @return List of books by the specified author
     */
    @Transactional(readOnly = true)
    public List<Book> findBooksByAuthor(String authorName) {
        return bookRepository.findByAuthorNameContainingIgnoreCase(authorName);
    }

    /**
     * Deletes a book by its ID.
     * @param id The book ID to delete
     * @throws BookVerseException if book not found or delete fails
     */
    public void deleteBook(Long id) throws BookVerseException {
        if (!bookRepository.existsById(id)) {
            throw new BookVerseException("Book with ID " + id + " not found");
        }
        
        try {
            bookRepository.deleteById(id);
        } catch (Exception e) {
            throw new BookVerseException("Failed to delete book: " + e.getMessage(), e);
        }
    }

    /**
     * Gets library statistics.
     * @return Library statistics object
     */
    @Transactional(readOnly = true)
    public LibraryStatistics getLibraryStatistics() {
        List<Book> allBooks = bookRepository.findAll();
        
        Map<String, Long> languageCount = allBooks.stream()
            .collect(Collectors.groupingBy(
                Book::getLanguage, 
                Collectors.counting()
            ));
        
        String mostCommonLanguage = languageCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Unknown");
        
        Map<String, Long> authorBookCount = allBooks.stream()
            .filter(book -> book.getAuthor() != null)
            .collect(Collectors.groupingBy(
                book -> book.getAuthor().getName(),
                Collectors.counting()
            ));
        
        String mostProlificAuthor = authorBookCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Unknown");
        
        long mostProlificAuthorBookCount = authorBookCount.values().stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0);
        
        double averageDownloads = allBooks.stream()
            .filter(book -> book.getDownloadCount() != null)
            .mapToInt(Book::getDownloadCount)
            .average()
            .orElse(0.0);
        
        return new LibraryStatistics(
            allBooks.size(),
            (int) allBooks.stream()
                .map(book -> book.getAuthor())
                .distinct()
                .count(),
            mostCommonLanguage,
            mostProlificAuthor,
            mostProlificAuthorBookCount,
            averageDownloads
        );
    }

    /**
     * Finds books with download count above a threshold.
     * @param minDownloads Minimum download count
     * @return List of popular books
     */
    @Transactional(readOnly = true)
    public List<Book> findPopularBooks(int minDownloads) {
        return bookRepository.findByDownloadCountGreaterThanOrderByDownloadCountDesc(minDownloads);
    }

    /**
     * Searches for books by publication year range.
     * @param startYear Start year (inclusive)
     * @param endYear End year (inclusive)
     * @return List of books published in the specified range
     */
    @Transactional(readOnly = true)
    public List<Book> findBooksByYearRange(int startYear, int endYear) {
        return bookRepository.findByPublicationYearBetweenOrderByPublicationYearDesc(startYear, endYear);
    }

    /**
     * Record type for library statistics.
     */
    public record LibraryStatistics(
        int totalBooks,
        int totalAuthors,
        String mostCommonLanguage,
        String mostProlificAuthor,
        long mostProlificAuthorBookCount,
        double averageDownloads
    ) {}
}
