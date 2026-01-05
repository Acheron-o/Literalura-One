package com.alura.literalura.service;

import com.alura.literalura.model.dto.DadosGutendex;
import com.alura.literalura.model.dto.LivroDTO;

import java.util.Arrays;
import java.util.Optional;

public class GutendexService {

    private final ApiConsumer apiConsumer;
    private final DataConverter dataConverter;
    private final String BASE_URL = "https://gutendex.com/books/?search=";

    public GutendexService() {
        this.apiConsumer = new ApiConsumer();
        this.dataConverter = new DataConverter();
    }
    
    // Constructor for testing (Dependency Injection)
    public GutendexService(ApiConsumer apiConsumer, DataConverter dataConverter) {
        this.apiConsumer = apiConsumer;
        this.dataConverter = dataConverter;
    }

    public Optional<LivroDTO> searchBook(String bookTitle) {
        String json = apiConsumer.fetchData(BASE_URL + bookTitle.replace(" ", "%20"));
        DadosGutendex data = dataConverter.convertData(json, DadosGutendex.class);

        if (data.results().isEmpty()) {
            return Optional.empty();
        }

        Optional<LivroDTO> exactMatch = data.results().stream()
                .filter(book -> book.title().equalsIgnoreCase(bookTitle))
                .findFirst();
        if (exactMatch.isPresent()) return exactMatch;

        Optional<LivroDTO> phraseMatch = data.results().stream()
                .filter(book -> book.title().toUpperCase().contains(bookTitle.toUpperCase()))
                .findFirst();
        if (phraseMatch.isPresent()) return phraseMatch;

        String[] keywords = bookTitle.toUpperCase().split(" ");
        Optional<LivroDTO> keywordMatch = data.results().stream()
                .filter(book -> {
                    String titleUpper = book.title().toUpperCase();
                    return Arrays.stream(keywords).allMatch(keyword -> 
                        titleUpper.matches(".*\\b" + keyword + "\\b.*")
                    );
                })
                .findFirst();
        
        return keywordMatch;
    }
}