package com.alura.literalura.model.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosGutendex(
        @JsonAlias("count") Integer count,
        @JsonAlias("next") String next,
        @JsonAlias("previous") String previous,
        @JsonAlias("results") List<LivroDTO> results
) {
}
