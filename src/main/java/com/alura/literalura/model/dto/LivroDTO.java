package com.alura.literalura.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LivroDTO(
        @JsonAlias("id") Long id,
        @JsonAlias("title") String title,
        @JsonAlias("authors") List<AutorDTO> authors,
        @JsonAlias("languages") List<String> languages,
        @JsonAlias("download_count") Integer downloadCount
) {
}
