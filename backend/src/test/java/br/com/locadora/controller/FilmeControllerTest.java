package br.com.locadora.controller;

import br.com.locadora.dto.FilmeRequestDTO;
import br.com.locadora.dto.FilmeResponseDTO;
import br.com.locadora.exception.GlobalExceptionHandler;
import br.com.locadora.exception.ResourceNotFoundException;
import br.com.locadora.service.FilmeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmeController.class)
@Import(GlobalExceptionHandler.class)
class FilmeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmeService filmeService;

    @Autowired
    private ObjectMapper objectMapper;

    private FilmeResponseDTO sampleResponse() {
        return new FilmeResponseDTO(1L, "Matrix", "Wachowski", 1999, "Sci-Fi", true);
    }

    @Test
    void findAllShouldReturn200() throws Exception {
        when(filmeService.findAll()).thenReturn(List.of(sampleResponse()));

        mockMvc.perform(get("/api/filmes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Matrix"));
    }

    @Test
    void findByIdShouldReturn200() throws Exception {
        when(filmeService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/filmes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Matrix"));
    }

    @Test
    void findByIdShouldReturn404WhenNotFound() throws Exception {
        when(filmeService.findById(99L)).thenThrow(new ResourceNotFoundException("Filme não encontrado com id: 99"));

        mockMvc.perform(get("/api/filmes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Filme não encontrado com id: 99"));
    }

    @Test
    void createShouldReturn201() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO("Matrix", "Wachowski", 1999, "Sci-Fi", true);
        when(filmeService.create(any())).thenReturn(sampleResponse());

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createShouldReturn400WhenTituloBlank() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO("", "Wachowski", 1999, "Sci-Fi", true);

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void createShouldReturn400WhenAnoNull() throws Exception {
        String json = """
                {"titulo":"Matrix","diretor":"Wachowski","ano":null,"genero":"Sci-Fi","disponivel":true}
                """;

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn400WhenAnoTooLow() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO("Test", "Dir", 1800, "Genre", true);

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn400WhenYearTooHigh() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO("Test", "Dir", 9999, "Genre", true);
        when(filmeService.create(any())).thenThrow(new IllegalArgumentException("Ano não pode ser superior a 2031"));

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void updateShouldReturn200() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO("Matrix Reloaded", "Wachowski", 2003, "Sci-Fi", true);
        when(filmeService.update(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/api/filmes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateShouldReturn404WhenNotFound() throws Exception {
        FilmeRequestDTO request = new FilmeRequestDTO("Test", "Dir", 2000, "Genre", true);
        when(filmeService.update(eq(99L), any())).thenThrow(new ResourceNotFoundException("Filme não encontrado com id: 99"));

        mockMvc.perform(put("/api/filmes/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn204() throws Exception {
        doNothing().when(filmeService).delete(1L);

        mockMvc.perform(delete("/api/filmes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturn404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Filme não encontrado com id: 99")).when(filmeService).delete(99L);

        mockMvc.perform(delete("/api/filmes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenAllFieldsNull() throws Exception {
        String json = "{}";

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShouldReturn400WhenDisponivelNull() throws Exception {
        String json = """
                {"titulo":"Matrix","diretor":"Wachowski","ano":1999,"genero":"Sci-Fi","disponivel":null}
                """;

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
