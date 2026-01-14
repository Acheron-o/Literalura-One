package com.alura.literalura;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Service to interact with Gutendex API.
 * Handles searching for books and converting API responses.
 */
@Service
public class GutendexService {
    
    private static final String GUTENDEX_BASE_URL = "https://gutendex.com/books/";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public GutendexService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Searches for books by title.
     * @param title The title to search for
     * @return List of books found
     */
    public List<GutendexBook> searchBooksByTitle(String title) {
        try {
            String url = GUTENDEX_BASE_URL + "?search=" + encodeSearchTerm(title);
            String response = restTemplate.getForObject(url, String.class);
            
            GutendexResponse gutendexResponse = objectMapper.readValue(response, GutendexResponse.class);
            return gutendexResponse.getResults();
            
        } catch (Exception e) {
            System.err.println("Error searching books by title: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Searches for books by author name.
     * @param authorName The author name to search for
     * @return List of books found
     */
    public List<GutendexBook> searchBooksByAuthor(String authorName) {
        try {
            String url = GUTENDEX_BASE_URL + "?search=" + encodeSearchTerm(authorName);
            String response = restTemplate.getForObject(url, String.class);
            
            GutendexResponse gutendexResponse = objectMapper.readValue(response, GutendexResponse.class);
            return gutendexResponse.getResults();
            
        } catch (Exception e) {
            System.err.println("Error searching books by author: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Searches for books by language.
     * @param language The language code (e.g., "en", "es", "pt")
     * @return List of books found
     */
    public List<GutendexBook> searchBooksByLanguage(String language) {
        try {
            String url = GUTENDEX_BASE_URL + "?languages=" + language.toLowerCase();
            String response = restTemplate.getForObject(url, String.class);
            
            GutendexResponse gutendexResponse = objectMapper.readValue(response, GutendexResponse.class);
            return gutendexResponse.getResults();
            
        } catch (Exception e) {
            System.err.println("Error searching books by language: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Gets a specific book by its Gutendex ID.
     * @param id The book ID
     * @return Optional containing the book if found
     */
    public Optional<GutendexBook> getBookById(Integer id) {
        try {
            String url = GUTENDEX_BASE_URL + "?ids=" + id;
            String response = restTemplate.getForObject(url, String.class);
            
            GutendexResponse gutendexResponse = objectMapper.readValue(response, GutendexResponse.class);
            List<GutendexBook> books = gutendexResponse.getResults();
            
            return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
            
        } catch (Exception e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Converts a GutendexBook to our local Book entity.
     * @param gutendexBook The API book
     * @return Local Book entity
     */
    public Book convertToBook(GutendexBook gutendexBook) {
        Book book = new Book(gutendexBook.getTitle());
        
        // Set language (use first language if available)
        if (gutendexBook.getLanguages() != null && !gutendexBook.getLanguages().isEmpty()) {
            book.setLanguage(gutendexBook.getLanguages().get(0));
        }
        
        // Set download count as a proxy for popularity
        book.setPublicationYear(gutendexBook.getDownload_count());
        
        return book;
    }
    
    /**
     * Converts a GutendexBook author to our local Author entity.
     * @param gutendexBook The API book containing author info
     * @return Local Author entity
     */
    public Author convertToAuthor(GutendexBook gutendexBook) {
        if (gutendexBook.getAuthors() == null || gutendexBook.getAuthors().isEmpty()) {
            return new Author("Unknown Author");
        }
        
        String authorName = gutendexBook.getAuthors().get(0);
        return new Author(authorName);
    }
    
    /**
     * Encodes search terms for URL compatibility.
     * @param term The search term
     * @return Encoded term
     */
    private String encodeSearchTerm(String term) {
        return term.trim().replaceAll("\\s+", "%20");
    }
}
