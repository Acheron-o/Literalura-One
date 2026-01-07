package com.alura.literalura.repository;

import com.alura.literalura.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Author entity operations.
 * Provides database access methods for author management with custom queries.
 */
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    /**
     * Finds an author by name (case-insensitive).
     * @param name The author name to search for
     * @return Optional containing the author if found
     */
    Optional<Author> findByNameIgnoreCase(String name);

    /**
     * Finds authors whose names contain the specified string (case-insensitive).
     * @param name The name fragment to search for
     * @return List of matching authors
     */
    List<Author> findByNameContainingIgnoreCase(String name);

    /**
     * Finds authors who were alive in a specific year.
     * @param year The year to check
     * @return List of authors alive in the specified year
     */
    @Query("SELECT a FROM Author a WHERE " +
           "(a.birthYear IS NULL OR a.birthYear <= :year) AND " +
           "(a.deathYear IS NULL OR a.deathYear >= :year)")
    List<Author> findAuthorsAliveInYear(@Param("year") Integer year);

    /**
     * Finds authors by nationality (case-insensitive).
     * @param nationality The nationality to search for
     * @return List of authors with the specified nationality
     */
    List<Author> findByNationalityIgnoreCase(String nationality);

    /**
     * Finds authors born within a year range, ordered by birth year.
     * @param startYear Start year (inclusive)
     * @param endYear End year (inclusive)
     * @return List of authors born in the specified range
     */
    List<Author> findByBirthYearBetweenOrderByBirthYear(Integer startYear, Integer endYear);

    /**
     * Finds authors who died within a year range, ordered by death year.
     * @param startYear Start year (inclusive)
     * @param endYear End year (inclusive)
     * @return List of authors who died in the specified range
     */
    List<Author> findByDeathYearBetweenOrderByDeathYear(Integer startYear, Integer endYear);

    /**
     * Finds authors by external ID.
     * @param externalId The external ID from the source API
     * @return Optional containing the author if found
     */
    Optional<Author> findByExternalId(String externalId);

    /**
     * Finds authors by UUID.
     * @param uuid The author UUID
     * @return Optional containing the author if found
     */
    Optional<Author> findByUuid(java.util.UUID uuid);

    /**
     * Counts authors by nationality.
     * @param nationality The nationality
     * @return Number of authors with the specified nationality
     */
    long countByNationalityIgnoreCase(String nationality);

    /**
     * Finds authors with no books associated.
     * @return List of authors without books
     */
    @Query("SELECT a FROM Author a WHERE SIZE(a.books) = 0")
    List<Author> findAuthorsWithoutBooks();

    /**
     * Finds authors born in a specific year.
     * @param year The birth year
     * @return List of authors born in the specified year
     */
    List<Author> findByBirthYear(Integer year);

    /**
     * Finds authors who died in a specific year.
     * @param year The death year
     * @return List of authors who died in the specified year
     */
    List<Author> findByDeathYear(Integer year);

    /**
     * Finds living authors (death year is null).
     * @return List of living authors
     */
    List<Author> findByDeathYearIsNull();

    /**
     * Finds deceased authors (death year is not null).
     * @return List of deceased authors
     */
    List<Author> findByDeathYearIsNotNull();

    /**
     * Custom query to find authors with their book counts.
     * @return List of authors with their book counts
     */
    @Query("SELECT new com.alura.literalura.service.AuthorManagementService$AuthorBookCount(" +
           "a.id, a.name, SIZE(a.books)) " +
           "FROM Author a " +
           "ORDER BY SIZE(a.books) DESC")
    List<com.alura.literalura.service.AuthorManagementService.AuthorBookCount> findAllAuthorsWithBookCounts();

    /**
     * Custom query to find prolific authors (with more than specified number of books).
     * @param minBooks Minimum number of books
     * @return List of prolific authors
     */
    @Query("SELECT a FROM Author a WHERE SIZE(a.books) >= :minBooks ORDER BY SIZE(a.books) DESC")
    List<Author> findProlificAuthors(@Param("minBooks") Integer minBooks);

    /**
     * Custom query to find authors by birth decade.
     * @param decadeStart Start year of the decade
     * @param decadeEnd End year of the decade
     * @return List of authors born in the specified decade
     */
    @Query("SELECT a FROM Author a WHERE " +
           "a.birthYear >= :decadeStart AND a.birthYear <= :decadeEnd " +
           "ORDER BY a.birthYear")
    List<Author> findAuthorsByBirthDecade(@Param("decadeStart") Integer decadeStart, 
                                         @Param("decadeEnd") Integer decadeEnd);

    /**
     * Custom query to get author statistics by nationality.
     * @return List of nationality statistics
     */
    @Query("SELECT a.nationality, COUNT(a) as authorCount " +
           "FROM Author a " +
           "WHERE a.nationality IS NOT NULL " +
           "GROUP BY a.nationality " +
           "ORDER BY authorCount DESC")
    List<Object[]> getAuthorCountByNationality();

    /**
     * Custom query to find authors by age range at death.
     * @param minAge Minimum age at death
     * @param maxAge Maximum age at death
     * @return List of authors who died within the specified age range
     */
    @Query("SELECT a FROM Author a WHERE " +
           "a.birthYear IS NOT NULL AND a.deathYear IS NOT NULL AND " +
           "(a.deathYear - a.birthYear) >= :minAge AND " +
           "(a.deathYear - a.birthYear) <= :maxAge " +
           "ORDER BY (a.deathYear - a.birthYear)")
    List<Author> findAuthorsByAgeAtDeath(@Param("minAge") Integer minAge, 
                                         @Param("maxAge") Integer maxAge);

    /**
     * Custom query to search authors by name, nationality, or biography.
     * @param searchTerm The search term
     * @return List of matching authors
     */
    @Query("SELECT a FROM Author a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.nationality) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.biography) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Author> searchAuthorsByMultipleFields(@Param("searchTerm") String searchTerm);

    /**
     * Custom query to find authors with books in a specific language.
     * @param language The language code
     * @return List of authors who have books in the specified language
     */
    @Query("SELECT DISTINCT a FROM Author a JOIN a.books b WHERE b.language = :language")
    List<Author> findAuthorsWithBooksInLanguage(@Param("language") String language);
}
