package com.alura.literalura.model;

import com.alura.literalura.model.dto.BookDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a book in the library system.
 * Contains metadata about the book and its relationship with authors.
 */
@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_book_title", columnList = "title"),
    @Index(name = "idx_book_language", columnList = "language"),
    @Index(name = "idx_book_isbn", columnList = "isbn")
})
public class Book {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 5000, nullable = false)
    @NotBlank(message = "Book title cannot be blank")
    @Size(max = 5000, message = "Book title cannot exceed 5000 characters")
    private String title;
    
    @Column(length = 20, unique = true)
    private String isbn;
    
    @Column(length = 10, nullable = false)
    @NotBlank(message = "Language cannot be blank")
    private String language;
    
    @Column(name = "download_count")
    private Integer downloadCount;
    
    @Column(name = "publication_year")
    private Integer publicationYear;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
    
    @Column(name = "external_id", unique = true)
    private String externalId;
    
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid;

    public Book() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.uuid = UUID.randomUUID();
    }

    public Book(BookDTO bookDTO) {
        this();
        this.title = bookDTO.title();
        this.language = !bookDTO.languages().isEmpty() ? bookDTO.languages().get(0) : "unknown";
        this.downloadCount = bookDTO.downloadCount();
        this.externalId = bookDTO.id();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return String.format(
            """
            📚 BOOK DETAILS
            ═══════════════════════════════════════
            Title: %s
            Author: %s
            Language: %s
            Downloads: %,d
            Publication Year: %s
            ISBN: %s
            UUID: %s
            ═══════════════════════════════════════
            """,
            title,
            (author != null ? author.getName() : "Unknown"),
            language.toUpperCase(),
            (downloadCount != null ? downloadCount : 0),
            (publicationYear != null ? publicationYear : "Unknown"),
            (isbn != null ? isbn : "Not available"),
            uuid
        );
    }
}
