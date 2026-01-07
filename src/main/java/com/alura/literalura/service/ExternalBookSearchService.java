package com.alura.literalura.service;

import com.alura.literalura.model.Book;
import com.alura.literalura.model.Author;
import com.alura.literalura.model.dto.BookDTO;
import com.alura.literalura.model.dto.AuthorDTO;
import com.alura.literalura.model.dto.ExternalApiResponse;
import com.alura.literalura.exception.BookVerseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Service for searching books from external APIs and saving them to the database.
 * Integrates with external book data providers like Gutendex.
 */
@Service
@Transactional
public class ExternalBookSearchService {
    
    private final BookManagementService bookService;
    private final AuthorManagementService authorService;
    private final ApiClientService apiClient;
    private final JsonDataConverterService dataConverter;
    
    private static final String GUTENDEX_BASE_URL = "https://gutendex.com/books/";
    private static final int MAX_SEARCH_RESULTS = 20;

    @Autowired
    public ExternalBookSearchService(BookManagementService bookService,
                                    AuthorManagementService authorService,
                                    ApiClientService apiClient,
                                    JsonDataConverterService dataConverter) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.apiClient = apiClient;
        this.dataConverter = dataConverter;
    }

    /**
     * Searches for a book by title and saves it to the database if found.
     * @param bookTitle The title to search for
     * @return Optional containing the saved book if found and saved
     * @throws BookVerseException if search fails or save operation fails
     */
    public Optional<Book> searchAndSaveBook(String bookTitle) throws BookVerseException {
        if (bookTitle == null || bookTitle.trim().isEmpty()) {
            throw new BookVerseException("Book title cannot be null or empty");
        }

        try {
            Optional<BookDTO> bookDTO = searchBookByTitle(bookTitle);
            if (bookDTO.isPresent()) {
                return Optional.of(saveBookFromDTO(bookDTO.get()));
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new BookVerseException("Failed to search and save book: " + e.getMessage(), e);
        }
    }

    /**
     * Searches for multiple books by a query and returns the results.
     * @param query The search query
     * @param limit Maximum number of results to return
     * @return List of found books (not saved to database)
     * @throws BookVerseException if search fails
     */
    @Transactional(readOnly = true)
    public List<BookDTO> searchBooks(String query, int limit) throws BookVerseException {
        if (query == null || query.trim().isEmpty()) {
            throw new BookVerseException("Search query cannot be null or empty");
        }

        try {
            String searchUrl = GUTENDEX_BASE_URL + "?search=" + encodeSearchQuery(query) + "&limit=" + Math.min(limit, MAX_SEARCH_RESULTS);
            String jsonResponse = apiClient.fetchData(searchUrl);
            ExternalApiResponse response = dataConverter.convertData(jsonResponse, ExternalApiResponse.class);
            
            return response.results().stream()
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BookVerseException("Failed to search books: " + e.getMessage(), e);
        }
    }

    /**
     * Searches for a book by title with intelligent matching.
     * @param bookTitle The title to search for
     * @return Optional containing the book DTO if found
     * @throws BookVerseException if search fails
     */
    @Transactional(readOnly = true)
    private Optional<BookDTO> searchBookByTitle(String bookTitle) throws BookVerseException {
        try {
            String searchUrl = GUTENDEX_BASE_URL + "?search=" + encodeSearchQuery(bookTitle);
            String jsonResponse = apiClient.fetchData(searchUrl);
            ExternalApiResponse response = dataConverter.convertData(jsonResponse, ExternalApiResponse.class);

            if (response.results().isEmpty()) {
                return Optional.empty();
            }

            // Try exact match first
            Optional<BookDTO> exactMatch = response.results().stream()
                .filter(book -> book.title().equalsIgnoreCase(bookTitle.trim()))
                .findFirst();
            
            if (exactMatch.isPresent()) {
                return exactMatch;
            }

            // Try partial match
            Optional<BookDTO> partialMatch = response.results().stream()
                .filter(book -> book.title().toLowerCase().contains(bookTitle.toLowerCase()))
                .findFirst();
            
            if (partialMatch.isPresent()) {
                return partialMatch;
            }

            // Try keyword matching
            String[] keywords = bookTitle.toLowerCase().split("\\s+");
            return response.results().stream()
                .filter(book -> {
                    String titleLower = book.title().toLowerCase();
                    return Arrays.stream(keywords)
                        .allMatch(keyword -> titleLower.contains(keyword));
                })
                .findFirst();

        } catch (Exception e) {
            throw new BookVerseException("Failed to search for book: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a book from a DTO to the database, including author management.
     * @param bookDTO The book DTO to save
     * @return The saved book
     * @throws BookVerseException if save operation fails
     */
    private Book saveBookFromDTO(BookDTO bookDTO) throws BookVerseException {
        try {
            // Create book entity
            Book book = new Book(bookDTO);

            // Handle author
            if (!bookDTO.authors().isEmpty()) {
                AuthorDTO authorDTO = bookDTO.authors().get(0);
                Author author = findOrCreateAuthorFromDTO(authorDTO);
                book.setAuthor(author);
            } else {
                // Create unknown author if no author information available
                Author unknownAuthor = authorService.findOrCreateAuthor("Unknown Author");
                book.setAuthor(unknownAuthor);
            }

            return bookService.saveBook(book);
        } catch (Exception e) {
            throw new BookVerseException("Failed to save book from DTO: " + e.getMessage(), e);
        }
    }

    /**
     * Finds or creates an author from an author DTO.
     * @param authorDTO The author DTO
     * @return The existing or newly created author
     * @throws BookVerseException if author operation fails
     */
    private Author findOrCreateAuthorFromDTO(AuthorDTO authorDTO) throws BookVerseException {
        try {
            // First try to find by external ID
            if (authorDTO.id() != null && !authorDTO.id().isEmpty()) {
                Optional<Author> existingByExternalId = authorService.getAllAuthors().stream()
                    .filter(author -> authorDTO.id().equals(author.getExternalId()))
                    .findFirst();
                
                if (existingByExternalId.isPresent()) {
                    return existingByExternalId.get();
                }
            }

            // Then try to find by name
            Optional<Author> existingByName = authorService.searchAuthorsByName(authorDTO.name()).stream()
                .findFirst();
            
            if (existingByName.isPresent()) {
                return existingByName.get();
            }

            // Create new author
            Author newAuthor = new Author(authorDTO);
            return authorService.saveAuthor(newAuthor);
        } catch (Exception e) {
            throw new BookVerseException("Failed to find or create author: " + e.getMessage(), e);
        }
    }

    /**
     * Encodes a search query for URL compatibility.
     * @param query The query to encode
     * @return The encoded query
     */
    private String encodeSearchQuery(String query) {
        return query.trim().replaceAll("\\s+", "%20");
    }

    /**
     * Searches for books by author name from external API.
     * @param authorName The author name to search for
     * @param limit Maximum number of results
     * @return List of books by the specified author
     * @throws BookVerseException if search fails
     */
    @Transactional(readOnly = true)
    public List<BookDTO> searchBooksByAuthor(String authorName, int limit) throws BookVerseException {
        try {
            String searchUrl = GUTENDEX_BASE_URL + "?search=" + encodeSearchQuery(authorName) + "&limit=" + Math.min(limit, MAX_SEARCH_RESULTS);
            String jsonResponse = apiClient.fetchData(searchUrl);
            ExternalApiResponse response = dataConverter.convertData(jsonResponse, ExternalApiResponse.class);
            
            return response.results().stream()
                .filter(book -> book.authors().stream()
                    .anyMatch(author -> author.name().toLowerCase().contains(authorName.toLowerCase())))
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BookVerseException("Failed to search books by author: " + e.getMessage(), e);
        }
    }

    /**
     * Searches for books by language from external API.
     * @param languageCode The language code to search for
     * @param limit Maximum number of results
     * @return List of books in the specified language
     * @throws BookVerseException if search fails
     */
    @Transactional(readOnly = true)
    public List<BookDTO> searchBooksByLanguage(String languageCode, int limit) throws BookVerseException {
        try {
            String searchUrl = GUTENDEX_BASE_URL + "?languages=" + languageCode.toLowerCase() + "&limit=" + Math.min(limit, MAX_SEARCH_RESULTS);
            String jsonResponse = apiClient.fetchData(searchUrl);
            ExternalApiResponse response = dataConverter.convertData(jsonResponse, ExternalApiResponse.class);
            
            return response.results().stream()
                .limit(limit)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BookVerseException("Failed to search books by language: " + e.getMessage(), e);
        }
    }
}
