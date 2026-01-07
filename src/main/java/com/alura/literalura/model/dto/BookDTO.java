package com.alura.literalura.model.dto;

import java.util.List;

/**
 * Data Transfer Object for Book information from external APIs.
 * Represents book data in a format suitable for API responses and data transformation.
 */
public record BookDTO(
    String id,
    String title,
    List<AuthorDTO> authors,
    List<String> languages,
    Integer downloadCount,
    List<String> subjects,
    List<String> bookshelves,
    String formats,
    Integer mediaType
) {
    
    /**
     * Creates a BookDTO with minimal required fields.
     * @param id The external book ID
     * @param title The book title
     * @param authors List of authors
     * @param languages List of language codes
     * @param downloadCount Download count
     */
    public BookDTO(String id, String title, List<AuthorDTO> authors, 
                   List<String> languages, Integer downloadCount) {
        this(id, title, authors, languages, downloadCount, null, null, null, null);
    }

    /**
     * Validates if the BookDTO contains essential information.
     * @return true if the DTO has valid title and at least one author
     */
    public boolean isValid() {
        return title != null && !title.trim().isEmpty() && 
               authors != null && !authors.isEmpty();
    }

    /**
     * Gets the primary language code.
     * @return The first language code if available, otherwise "unknown"
     */
    public String primaryLanguage() {
        return (languages != null && !languages.isEmpty()) ? languages.get(0) : "unknown";
    }

    /**
     * Gets the primary author.
     * @return The first author if available, otherwise null
     */
    public AuthorDTO primaryAuthor() {
        return (authors != null && !authors.isEmpty()) ? authors.get(0) : null;
    }

    /**
     * Checks if the book has multiple authors.
     * @return true if the book has more than one author
     */
    public boolean hasMultipleAuthors() {
        return authors != null && authors.size() > 1;
    }

    /**
     * Gets the total number of subjects.
     * @return Number of subjects, or 0 if subjects list is null
     */
    public int subjectCount() {
        return subjects != null ? subjects.size() : 0;
    }

    /**
     * Gets the total number of bookshelves.
     * @return Number of bookshelves, or 0 if bookshelves list is null
     */
    public int bookshelfCount() {
        return bookshelves != null ? bookshelves.size() : 0;
    }

    /**
     * Checks if the book is popular based on download count.
     * @return true if download count is greater than 1000
     */
    public boolean isPopular() {
        return downloadCount != null && downloadCount > 1000;
    }

    /**
     * Gets a formatted representation of the book for display.
     * @return Formatted string with key book information
     */
    @Override
    public String toString() {
        return String.format(
            "BookDTO{id='%s', title='%s', authors=%d, language='%s', downloads=%d}",
            id, title, authors.size(), primaryLanguage(), downloadCount
        );
    }
}
