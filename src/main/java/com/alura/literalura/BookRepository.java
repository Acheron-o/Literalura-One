package com.alura.literalura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Handles database operations for books.
 * Spring Data JPA automatically creates the implementation.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Find books by title (case insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Find books by language
    List<Book> findByLanguageIgnoreCase(String language);

    // Find books by author name
    @Query("SELECT b FROM Book b WHERE b.author.name LIKE %:authorName%")
    List<Book> findByAuthorName(String authorName);
}
