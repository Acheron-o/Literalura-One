package com.alura.literalura.service;

import com.alura.literalura.model.dto.DadosGutendex;
import com.alura.literalura.model.dto.LivroDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GutendexServiceUnitTest {

    @Mock
    ApiConsumer apiConsumer;

    @Mock
    DataConverter dataConverter;

    @InjectMocks
    GutendexService service;

    @Test
    @DisplayName("Should return empty when API returns no results")
    void whenNoResults_returnsEmpty() {
        String emptyJson = "{}";
        when(apiConsumer.fetchData(anyString())).thenReturn(emptyJson);
        when(dataConverter.convertData(emptyJson, DadosGutendex.class))
            .thenReturn(new DadosGutendex(0, null, null, new ArrayList<>()));

        var result = service.searchBook("Mushoku Tensei Volume 99");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find exact match")
    void whenExactSearch_returnsCorrectBook() {
        LivroDTO mushokuTensei = new LivroDTO(42L, "Mushoku Tensei Jobless Reincarnation", 
            new ArrayList<>(), List.of("en", "ja"), 350);
        DadosGutendex responseData = new DadosGutendex(1, null, null, List.of(mushokuTensei));

        String jsonResponse = "{\"count\":1,\"results\":[...]}";
        when(apiConsumer.fetchData(anyString())).thenReturn(jsonResponse);
        when(dataConverter.convertData(jsonResponse, DadosGutendex.class)).thenReturn(responseData);

        var result = service.searchBook("Mushoku Tensei Jobless Reincarnation");

        assertThat(result).isPresent();
        assertThat(result.get().title()).isEqualTo("Mushoku Tensei Jobless Reincarnation");
    }

    @Test
    @DisplayName("Should not give wrong partial match (Ex: Tensei in Tensei no Shitara)")
    void whenPartialSearch_shouldNotMakeIncorrectMatch() {
        // Testing that searching "Tensei" should not return "Tensei no Shitara"
        LivroDTO wrongBook = new LivroDTO(15L, "Tensei no Shitara Slime Datta Ken", 
            new ArrayList<>(), List.of("ja"), 250);
        DadosGutendex apiResponse = new DadosGutendex(1, null, null, List.of(wrongBook));

        String mockJson = "{\"count\":1}";
        when(apiConsumer.fetchData(anyString())).thenReturn(mockJson);
        when(dataConverter.convertData(mockJson, DadosGutendex.class)).thenReturn(apiResponse);

        var result = service.searchBook("Tensei");

        assertThat(result).isEmpty();
    }
}
