package br.com.locadora.resilience;

import br.com.locadora.controller.FilmeController;
import br.com.locadora.exception.GlobalExceptionHandler;
import br.com.locadora.exception.ResourceNotFoundException;
import br.com.locadora.service.FilmeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmeController.class)
@Import(GlobalExceptionHandler.class)
class NetworkErrorSimulationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FilmeService filmeService;

    @Test
    void serviceRuntimeExceptionShouldReturn500WithSafeMessage() throws Exception {
        when(filmeService.findAll()).thenThrow(new RuntimeException("DB connection failed"));

        mockMvc.perform(get("/api/filmes"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void serviceNullPointerShouldReturn500WithSafeMessage() throws Exception {
        when(filmeService.findById(1L)).thenThrow(new NullPointerException());

        mockMvc.perform(get("/api/filmes/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }

    @Test
    void shouldNotLeakStackTraceOnError() throws Exception {
        when(filmeService.findAll()).thenThrow(new RuntimeException("sensitive.db.password=secret"));

        mockMvc.perform(get("/api/filmes"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("sensitive"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("stackTrace"))));
    }

    @Test
    void serviceExceptionOnCreateShouldReturn500() throws Exception {
        when(filmeService.create(any())).thenThrow(new RuntimeException("DB write failed"));

        String json = """
                {"titulo":"T","diretor":"D","ano":2000,"genero":"G","disponivel":true}
                """;

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }

    @Test
    void concurrentResourceNotFoundShouldReturn404() throws Exception {
        when(filmeService.findById(1L)).thenThrow(new ResourceNotFoundException("Filme não encontrado com id: 1"));

        mockMvc.perform(get("/api/filmes/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Filme não encontrado com id: 1"));
    }

    @Test
    void illegalArgumentFromServiceShouldReturn400() throws Exception {
        when(filmeService.create(any())).thenThrow(new IllegalArgumentException("Ano inválido"));

        String json = """
                {"titulo":"T","diretor":"D","ano":9999,"genero":"G","disponivel":true}
                """;

        mockMvc.perform(post("/api/filmes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ano inválido"));
    }
}
