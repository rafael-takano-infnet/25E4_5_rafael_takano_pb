package br.com.locadora.fuzz;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmeApiFuzzTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> postJson(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForEntity("/api/filmes", new HttpEntity<>(json, headers), String.class);
    }

    @Test
    void sqlInjectionInTitulo() {
        String json = """
                {"titulo":"'; DROP TABLE filmes; --","diretor":"Dir","ano":2000,"genero":"G","disponivel":true}
                """;
        ResponseEntity<String> response = postJson(json);
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().contains("Exception"));
    }

    @Test
    void xssInTitulo() {
        String json = """
                {"titulo":"<script>alert('xss')</script>","diretor":"Dir","ano":2000,"genero":"G","disponivel":true}
                """;
        ResponseEntity<String> response = postJson(json);
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void unicodeCharacters() {
        String json = """
                {"titulo":"Película 日本語 émojis 🎬","diretor":"导演","ano":2000,"genero":"Gênero","disponivel":true}
                """;
        ResponseEntity<String> response = postJson(json);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void emptyJsonBody() {
        ResponseEntity<String> response = postJson("{}");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().contains("stackTrace"));
    }

    @Test
    void nullJsonBody() {
        ResponseEntity<String> response = postJson("null");
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void malformedJson() {
        ResponseEntity<String> response = postJson("{invalid json}");
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void extremelyLongString() {
        String longString = "A".repeat(10000);
        String json = String.format(
                "{\"titulo\":\"%s\",\"diretor\":\"Dir\",\"ano\":2000,\"genero\":\"G\",\"disponivel\":true}", longString);
        ResponseEntity<String> response = postJson(json);
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().contains("stackTrace"));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1887, Integer.MAX_VALUE})
    void boundaryYearValues(int year) {
        String json = String.format(
                "{\"titulo\":\"T\",\"diretor\":\"D\",\"ano\":%d,\"genero\":\"G\",\"disponivel\":true}", year);
        ResponseEntity<String> response = postJson(json);
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertFalse(response.getBody().contains("stackTrace"));
    }

    @Test
    void negativeIdShouldNotReturn500() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/filmes/-1", String.class);
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void zeroIdShouldNotReturn500() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/filmes/0", String.class);
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void extraFieldsShouldBeIgnored() {
        String json = """
                {"titulo":"T","diretor":"D","ano":2000,"genero":"G","disponivel":true,"hack":"value","id":999}
                """;
        ResponseEntity<String> response = postJson(json);
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
