package com.alura.literalura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Represents a book from Gutendex API response.
 * Simple DTO to map API response fields.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GutendexBook {
    
    private Integer id;
    private String title;
    private List<String> authors;
    private List<String> languages;
    private Integer download_count;
    private List<String> subjects;
    
    // Default constructor
    public GutendexBook() {}
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public List<String> getAuthors() {
        return authors;
    }
    
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }
    
    public List<String> getLanguages() {
        return languages;
    }
    
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    
    public Integer getDownload_count() {
        return download_count;
    }
    
    public void setDownload_count(Integer download_count) {
        this.download_count = download_count;
    }
    
    public List<String> getSubjects() {
        return subjects;
    }
    
    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }
    
    @Override
    public String toString() {
        return "Title: " + title + 
               (authors != null && !authors.isEmpty() ? " by " + String.join(", ", authors) : "") +
               (languages != null && !languages.isEmpty() ? " [" + String.join(", ", languages) + "]" : "");
    }
}
