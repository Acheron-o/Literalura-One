package com.alura.literalura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Represents the complete Gutendex API response.
 * Contains pagination info and the list of books.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GutendexResponse {
    
    private Integer count;
    private String next;
    private String previous;
    private List<GutendexBook> results;
    
    // Default constructor
    public GutendexResponse() {}
    
    // Getters and setters
    public Integer getCount() {
        return count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
    
    public String getNext() {
        return next;
    }
    
    public void setNext(String next) {
        this.next = next;
    }
    
    public String getPrevious() {
        return previous;
    }
    
    public void setPrevious(String previous) {
        this.previous = previous;
    }
    
    public List<GutendexBook> getResults() {
        return results;
    }
    
    public void setResults(List<GutendexBook> results) {
        this.results = results;
    }
}
