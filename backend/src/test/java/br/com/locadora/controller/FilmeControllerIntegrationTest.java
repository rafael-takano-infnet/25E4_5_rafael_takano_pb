package br.com.locadora.controller;

import br.com.locadora.dto.FilmeRequestDTO;
import br.com.locadora.dto.FilmeResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmeControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private FilmeRequestDTO createRequest() {
        return new FilmeRequestDTO("Matrix", "Wachowski", 1999, "Sci-Fi", true);
    }

    @Test
    void fullCrudLifecycle() {
        // CREATE
        ResponseEntity<FilmeResponseDTO> createResponse = restTemplate.postForEntity(
                "/api/filmes", createRequest(), FilmeResponseDTO.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        Long id = createResponse.getBody().id();
        assertNotNull(id);

        // READ
        ResponseEntity<FilmeResponseDTO> getResponse = restTemplate.getForEntity(
                "/api/filmes/" + id, FilmeResponseDTO.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals("Matrix", getResponse.getBody().titulo());

        // UPDATE
        FilmeRequestDTO updateRequest = new FilmeRequestDTO("Matrix Reloaded", "Wachowski", 2003, "Sci-Fi", false);
        ResponseEntity<FilmeResponseDTO> updateResponse = restTemplate.exchange(
                "/api/filmes/" + id, HttpMethod.PUT, new HttpEntity<>(updateRequest), FilmeResponseDTO.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertEquals("Matrix Reloaded", updateResponse.getBody().titulo());
        assertFalse(updateResponse.getBody().disponivel());

        // DELETE
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/filmes/" + id, HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // VERIFY DELETED
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                "/api/filmes/" + id, String.class);
        assertEquals(HttpStatus.NOT_FOUND, verifyResponse.getStatusCode());
    }

    @Test
    void findAllShouldReturnEmpty() {
        ResponseEntity<FilmeResponseDTO[]> response = restTemplate.getForEntity(
                "/api/filmes", FilmeResponseDTO[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);
    }

    @Test
    void createWithInvalidDataShouldReturn400() {
        FilmeRequestDTO invalid = new FilmeRequestDTO("", "", null, "", null);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/filmes", invalid, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void findByNonExistentIdShouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/filmes/999", String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteNonExistentShouldReturn404() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/filmes/999", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateNonExistentShouldReturn404() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/filmes/999", HttpMethod.PUT, new HttpEntity<>(createRequest()), String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
