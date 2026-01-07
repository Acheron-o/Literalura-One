package com.alura.literalura.service;

import com.alura.literalura.model.Author;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.exception.BookVerseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing author operations.
 * Provides business logic for author CRUD operations and queries.
 */
@Service
@Transactional
public class AuthorManagementService {
    
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorManagementService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Saves a new author to the database.
     * @param author The author to save
     * @return The saved author with generated ID
     * @throws BookVerseException if author already exists or save fails
     */
    public Author saveAuthor(Author author) throws BookVerseException {
        try {
            // Check for duplicate by name
            Optional<Author> existing = authorRepository.findByNameIgnoreCase(author.getName());
            if (existing.isPresent()) {
                throw new BookVerseException("Author with name '" + author.getName() + 
                                           "' already exists.");
            }
            
            return authorRepository.save(author);
        } catch (Exception e) {
            throw new BookVerseException("Failed to save author: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing author.
     * @param id The author ID to update
     * @param authorDetails The updated author details
     * @return The updated author
     * @throws BookVerseException if author not found or update fails
     */
    public Author updateAuthor(Long id, Author authorDetails) throws BookVerseException {
        Author existingAuthor = getAuthorById(id);
        
        existingAuthor.setName(authorDetails.getName());
        existingAuthor.setBirthYear(authorDetails.getBirthYear());
        existingAuthor.setDeathYear(authorDetails.getDeathYear());
        existingAuthor.setBirthDate(authorDetails.getBirthDate());
        existingAuthor.setDeathDate(authorDetails.getDeathDate());
        existingAuthor.setNationality(authorDetails.getNationality());
        existingAuthor.setBiography(authorDetails.getBiography());
        
        return authorRepository.save(existingAuthor);
    }

    /**
     * Retrieves an author by their ID.
     * @param id The author ID
     * @return The author if found
     * @throws BookVerseException if author not found
     */
    @Transactional(readOnly = true)
    public Author getAuthorById(Long id) throws BookVerseException {
        return authorRepository.findById(id)
            .orElseThrow(() -> new BookVerseException("Author with ID " + id + " not found"));
    }

    /**
     * Retrieves all authors in the database.
     * @return List of all authors
     */
    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    /**
     * Searches for authors by name.
     * @param name The name to search for (case-insensitive, partial match)
     * @return List of matching authors
     */
    @Transactional(readOnly = true)
    public List<Author> searchAuthorsByName(String name) {
        return authorRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Finds authors who were alive in a specific year.
     * @param year The year to check
     * @return List of authors alive in the specified year
     */
    @Transactional(readOnly = true)
    public List<Author> findAuthorsAliveInYear(int year) {
        return authorRepository.findAuthorsAliveInYear(year);
    }

    /**
     * Finds authors by nationality.
     * @param nationality The nationality to filter by
     * @return List of authors with the specified nationality
     */
    @Transactional(readOnly = true)
    public List<Author> findAuthorsByNationality(String nationality) {
        return authorRepository.findByNationalityIgnoreCase(nationality);
    }

    /**
     * Finds authors born in a specific year range.
     * @param startYear Start year (inclusive)
     * @param endYear End year (inclusive)
     * @return List of authors born in the specified range
     */
    @Transactional(readOnly = true)
    public List<Author> findAuthorsByBirthYearRange(int startYear, int endYear) {
        return authorRepository.findByBirthYearBetweenOrderByBirthYear(startYear, endYear);
    }

    /**
     * Deletes an author by their ID.
     * @param id The author ID to delete
     * @throws BookVerseException if author not found or delete fails
     */
    public void deleteAuthor(Long id) throws BookVerseException {
        if (!authorRepository.existsById(id)) {
            throw new BookVerseException("Author with ID " + id + " not found");
        }
        
        try {
            authorRepository.deleteById(id);
        } catch (Exception e) {
            throw new BookVerseException("Failed to delete author: " + e.getMessage(), e);
        }
    }

    /**
     * Finds or creates an author by name.
     * @param name The author name
     * @return The existing or newly created author
     */
    public Author findOrCreateAuthor(String name) {
        return authorRepository.findByNameIgnoreCase(name)
            .orElseGet(() -> {
                Author newAuthor = new Author();
                newAuthor.setName(name);
                return authorRepository.save(newAuthor);
            });
    }

    /**
     * Gets authors with their book counts.
     * @return List of authors with their respective book counts
     */
    @Transactional(readOnly = true)
    public List<AuthorBookCount> getAuthorsWithBookCounts() {
        return authorRepository.findAllAuthorsWithBookCounts();
    }

    /**
     * Finds prolific authors (authors with more than a specified number of books).
     * @param minBooks Minimum number of books
     * @return List of prolific authors
     */
    @Transactional(readOnly = true)
    public List<Author> findProlificAuthors(int minBooks) {
        return authorRepository.findProlificAuthors(minBooks);
    }

    /**
     * Record for author book count information.
     */
    public record AuthorBookCount(
        Long authorId,
        String authorName,
        Integer bookCount
    ) {}
}
