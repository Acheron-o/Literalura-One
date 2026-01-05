package com.alura.literalura.model;

import com.alura.literalura.model.dto.AutorDTO;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 500)
    private String name;

    private Integer birthYear;
    private Integer deathYear;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Livro> books = new ArrayList<>();

    public Autor() {}

    public Autor(AutorDTO autorDTO) {
        this.name = autorDTO.name();
        this.birthYear = autorDTO.birthYear();
        this.deathYear = autorDTO.deathYear();
    }

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

    public List<Livro> getBooks() {
        return books;
    }

    public void setBooks(List<Livro> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return "Author: " + name +
                " (Born: " + (birthYear != null ? birthYear : "?") +
                " - Died: " + (deathYear != null ? deathYear : "?") + ")";
    }
}
