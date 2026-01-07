package com.alura.literalura.model;

import com.alura.literalura.model.dto.AuthorDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an author in the library system.
 * Contains biographical information and relationships to books.
 */
@Entity
@Table(name = "authors", indexes = {
    @Index(name = "idx_author_name", columnList = "name"),
    @Index(name = "idx_author_birth_year", columnList = "birth_year"),
    @Index(name = "idx_author_death_year", columnList = "death_year")
})
public class Author {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 500, nullable = false)
    @NotBlank(message = "Author name cannot be blank")
    @Size(max = 500, message = "Author name cannot exceed 500 characters")
    private String name;
    
    @Column(name = "birth_year")
    private Integer birthYear;
    
    @Column(name = "death_year")
    private Integer deathYear;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "death_date")
    private LocalDate deathDate;
    
    @Column(name = "nationality", length = 100)
    private String nationality;
    
    @Column(name = "biography", length = 2000)
    private String biography;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();
    
    @Column(name = "external_id", unique = true)
    private String externalId;
    
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid;

    public Author() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.uuid = UUID.randomUUID();
    }

    public Author(AuthorDTO authorDTO) {
        this();
        this.name = authorDTO.name();
        this.birthYear = authorDTO.birthYear();
        this.deathYear = authorDTO.deathYear();
        this.externalId = authorDTO.id();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business logic methods
    public boolean isAliveInYear(int year) {
        return (birthYear == null || birthYear <= year) && 
               (deathYear == null || deathYear >= year);
    }

    public int getAgeAtDeath() {
        if (deathYear != null && birthYear != null) {
            return deathYear - birthYear;
        }
        return -1;
    }

    public int getBookCount() {
        return books != null ? books.size() : 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(LocalDate deathDate) {
        this.deathDate = deathDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
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

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
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
            👤 AUTHOR DETAILS
            ═══════════════════════════════════════
            Name: %s
            Birth Year: %s
            Death Year: %s
            Age at Death: %s
            Nationality: %s
            Books Published: %d
            UUID: %s
            ═══════════════════════════════════════
            """,
            name,
            (birthYear != null ? birthYear : "Unknown"),
            (deathYear != null ? deathYear : "Still alive"),
            (getAgeAtDeath() > 0 ? getAgeAtDeath() + " years" : "Unknown"),
            (nationality != null ? nationality : "Unknown"),
            getBookCount(),
            uuid
        );
    }
}
