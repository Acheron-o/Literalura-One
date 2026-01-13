package com.alura.literalura;

import jakarta.persistence.*;

/**
 * Represents an author in our book library.
 * Simple class with basic information about authors.
 */
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer birthYear;

    private Integer deathYear;

    // Default constructor
    public Author() {}

    // Constructor with name
    public Author(String name) {
        this.name = name;
    }

    // Getters and setters
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

    @Override
    public String toString() {
        return "Author: " + name + 
               (birthYear != null ? " (" + birthYear + "-" + (deathYear != null ? deathYear : "present") + ")" : "");
    }
}
