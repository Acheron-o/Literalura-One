package com.alura.literalura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Handles database operations for authors.
 * Spring Data JPA automatically creates the implementation.
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    // Find authors by name (case insensitive)
    List<Author> findByNameContainingIgnoreCase(String name);

    // Find authors alive in a specific year
    @Query("SELECT a FROM Author a WHERE " +
           "(a.birthYear IS NULL OR a.birthYear <= :year) AND " +
           "(a.deathYear IS NULL OR a.deathYear >= :year)")
    List<Author> findAuthorsAliveInYear(Integer year);
}
