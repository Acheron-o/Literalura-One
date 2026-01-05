package com.alura.literalura.model;

import com.alura.literalura.model.dto.LivroDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 5000)
    private String title;

    @ManyToOne
    private Autor author;

    private String language;
    private Integer downloadCount;

    public Livro() {}

    public Livro(LivroDTO livroDTO) {
        this.title = livroDTO.title();
        this.language = !livroDTO.languages().isEmpty() ? livroDTO.languages().get(0) : null;
        this.downloadCount = livroDTO.downloadCount();
    }

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

    public Autor getAuthor() {
        return author;
    }

    public void setAuthor(Autor author) {
        this.author = author;
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

    @Override
    public String toString() {
        return """
                ----- BOOK -----
                Title: %s
                Author: %s
                Language: %s
                Downloads: %d
                -----------------
                """.formatted(title, (author != null ? author.getName() : "Unknown"), language, downloadCount);
    }
}