package com.alura.literalura.model.dto;

import java.util.List;

/**
 * Data Transfer Object for external API responses.
 * Represents the structure of API responses from book data providers like Gutendex.
 */
public record ExternalApiResponse(
    Integer count,
    String next,
    String previous,
    List<BookDTO> results
) {
    
    /**
     * Creates an ExternalApiResponse with empty results.
     * @param count Total count of results
     * @param next URL for next page
     * @param previous URL for previous page
     */
    public ExternalApiResponse(Integer count, String next, String previous) {
        this(count, next, previous, List.of());
    }

    /**
     * Creates an ExternalApiResponse with only results.
     * @param results List of book results
     */
    public ExternalApiResponse(List<BookDTO> results) {
        this(results.size(), null, null, results);
    }

    /**
     * Checks if there are more results available.
     * @return true if next page URL is provided
     */
    public boolean hasNextPage() {
        return next != null && !next.trim().isEmpty();
    }

    /**
     * Checks if there is a previous page available.
     * @return true if previous page URL is provided
     */
    public boolean hasPreviousPage() {
        return previous != null && !previous.trim().isEmpty();
    }

    /**
     * Checks if the response contains any results.
     * @return true if results list is not empty
     */
    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }

    /**
     * Gets the number of results in this response.
     * @return Number of results, or 0 if results is null
     */
    public int getResultCount() {
        return results != null ? results.size() : 0;
    }

    /**
     * Checks if this is the first page of results.
     * @return true if there is no previous page
     */
    public boolean isFirstPage() {
        return !hasPreviousPage();
    }

    /**
     * Checks if this is the last page of results.
     * @return true if there is no next page
     */
    public boolean isLastPage() {
        return !hasNextPage();
    }

    /**
     * Gets the first book from the results.
     * @return First book if available, otherwise null
     */
    public BookDTO getFirstResult() {
        return hasResults() ? results.get(0) : null;
    }

    /**
     * Gets the last book from the results.
     * @return Last book if available, otherwise null
     */
    public BookDTO getLastResult() {
        return hasResults() ? results.get(results.size() - 1) : null;
    }

    /**
     * Filters results to only include valid books.
     * @return List of valid books
     */
    public List<BookDTO> getValidResults() {
        if (!hasResults()) {
            return List.of();
        }
        
        return results.stream()
            .filter(BookDTO::isValid)
            .toList();
    }

    /**
     * Gets books with download count above a threshold.
     * @param minDownloads Minimum download count
     * @return List of popular books
     */
    public List<BookDTO> getPopularBooks(int minDownloads) {
        if (!hasResults()) {
            return List.of();
        }
        
        return results.stream()
            .filter(book -> book.downloadCount() != null && book.downloadCount() >= minDownloads)
            .toList();
    }

    /**
     * Gets books in a specific language.
     * @param languageCode The language code to filter by
     * @return List of books in the specified language
     */
    public List<BookDTO> getBooksByLanguage(String languageCode) {
        if (!hasResults()) {
            return List.of();
        }
        
        return results.stream()
            .filter(book -> book.languages() != null && 
                            book.languages().stream()
                                .anyMatch(lang -> lang.equalsIgnoreCase(languageCode)))
            .toList();
    }

    /**
     * Gets books by a specific author.
     * @param authorName The author name to search for
     * @return List of books by the specified author
     */
    public List<BookDTO> getBooksByAuthor(String authorName) {
        if (!hasResults()) {
            return List.of();
        }
        
        return results.stream()
            .filter(book -> book.authors() != null && 
                            book.authors().stream()
                                .anyMatch(author -> author.name().toLowerCase().contains(authorName.toLowerCase())))
            .toList();
    }

    /**
     * Gets a summary of the response.
     * @return Formatted summary string
     */
    @Override
    public String toString() {
        return String.format(
            "ExternalApiResponse{count=%d, results=%d, hasNext=%s, hasPrevious=%s}",
            count, getResultCount(), hasNextPage(), hasPreviousPage()
        );
    }
}
