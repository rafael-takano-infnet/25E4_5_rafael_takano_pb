package br.com.locadora.dto;

import br.com.locadora.model.Filme;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FilmeMapperTest {

    private final FilmeMapper filmeMapper = new FilmeMapper();

    @Test
    void toEntityShouldCopyRequestFields() {
        FilmeRequestDTO request = new FilmeRequestDTO("Matrix", "Wachowski", 1999, "Sci-Fi", false);

        Filme filme = filmeMapper.toEntity(request);

        assertEquals("Matrix", filme.getTitulo());
        assertEquals("Wachowski", filme.getDiretor());
        assertEquals(1999, filme.getAno());
        assertEquals("Sci-Fi", filme.getGenero());
        assertFalse(filme.getDisponivel());
    }

    @Test
    void toResponseListShouldConvertEveryEntity() {
        Filme filme = new Filme("Matrix", "Wachowski", 1999, "Sci-Fi");
        filme.setId(1L);

        List<FilmeResponseDTO> result = filmeMapper.toResponseList(List.of(filme));

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().id());
        assertEquals("Matrix", result.getFirst().titulo());
    }
}
