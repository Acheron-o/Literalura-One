package com.alura.literalura.repository;

import com.alura.literalura.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entity operations.
 * Provides database access methods for book management with custom queries.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Finds books by their language.
     * @param language The language code to search for
     * @return List of books in the specified language
     */
    List<Book> findByLanguage(String language);

    /**
     * Finds books by title containing the specified string (case-insensitive).
     * @param title The title fragment to search for
     * @return List of matching books
     */
    List<Book> findByTitleContainingIgnoreCase(String title);

    /**
     * Finds a book by its exact title and author ID.
     * @param title The exact book title
     * @param authorId The author ID
     * @return Optional containing the book if found
     */
    Optional<Book> findByTitleAndAuthorId(String title, Long authorId);

    /**
     * Finds books by author name (case-insensitive partial match).
     * @param authorName The author name fragment to search for
     * @return List of books by matching authors
     */
    List<Book> findByAuthorNameContainingIgnoreCase(String authorName);

    /**
     * Finds books with download count greater than the specified threshold, ordered by download count descending.
     * @param minDownloads Minimum download count
     * @return List of popular books
     */
    List<Book> findByDownloadCountGreaterThanOrderByDownloadCountDesc(Integer minDownloads);

    /**
     * Finds books published within a year range, ordered by publication year descending.
     * @param startYear Start year (inclusive)
     * @param endYear End year (inclusive)
     * @return List of books published in the specified range
     */
    List<Book> findByPublicationYearBetweenOrderByPublicationYearDesc(Integer startYear, Integer endYear);

    /**
     * Finds books by external ID.
     * @param externalId The external ID from the source API
     * @return Optional containing the book if found
     */
    Optional<Book> findByExternalId(String externalId);

    /**
     * Finds books by UUID.
     * @param uuid The book UUID
     * @return Optional containing the book if found
     */
    Optional<Book> findByUuid(java.util.UUID uuid);

    /**
     * Counts books by language.
     * @param language The language code
     * @return Number of books in the specified language
     */
    long countByLanguage(String language);

    /**
     * Finds books with null or empty ISBN.
     * @return List of books missing ISBN
     */
    List<Book> findByIsbnIsNullOrIsbnEmpty();

    /**
     * Custom query to find books with author information using JOIN FETCH.
     * @param id The book ID
     * @return Optional containing the book with author loaded
     */
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.author WHERE b.id = :id")
    Optional<Book> findByIdWithAuthor(@Param("id") Long id);

    /**
     * Custom query to find all books with their authors loaded.
     * @return List of books with authors
     */
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.author ORDER BY b.title")
    List<Book> findAllWithAuthors();

    /**
     * Custom query to find books by multiple criteria.
     * @param title Book title fragment (optional)
     * @param language Language code (optional)
     * @param minDownloads Minimum download count (optional)
     * @param authorId Author ID (optional)
     * @return List of books matching the criteria
     */
    @Query("SELECT b FROM Book b LEFT JOIN b.author a " +
           "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:language IS NULL OR b.language = :language) " +
           "AND (:minDownloads IS NULL OR b.downloadCount >= :minDownloads) " +
           "AND (:authorId IS NULL OR a.id = :authorId)")
    List<Book> findBooksByMultipleCriteria(
        @Param("title") String title,
        @Param("language") String language,
        @Param("minDownloads") Integer minDownloads,
        @Param("authorId") Long authorId
    );

    /**
     * Custom query to get book statistics by language.
     * @return List of language statistics
     */
    @Query("SELECT b.language, COUNT(b) as bookCount " +
           "FROM Book b " +
           "GROUP BY b.language " +
           "ORDER BY bookCount DESC")
    List<Object[]> getBookCountByLanguage();

    /**
     * Custom query to find the most downloaded books.
     * @param limit Maximum number of results
     * @return List of most downloaded books
     */
    @Query("SELECT b FROM Book b WHERE b.downloadCount IS NOT NULL ORDER BY b.downloadCount DESC")
    List<Book> findMostDownloadedBooks();

    /**
     * Custom query to search books by title or author name.
     * @param searchTerm The search term
     * @return List of matching books
     */
    @Query("SELECT b FROM Book b LEFT JOIN b.author a " +
           "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Book> searchBooksByTitleOrAuthor(@Param("searchTerm") String searchTerm);

    /**
     * Custom query to find books published in a specific year.
     * @param year The publication year
     * @return List of books published in the specified year
     */
    @Query("SELECT b FROM Book b WHERE b.publicationYear = :year ORDER BY b.downloadCount DESC")
    List<Book> findBooksByPublicationYear(@Param("year") Integer year);
}
