package br.com.locadora.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilmeTest {

    @Test
    void defaultConstructorShouldSetDisponivelTrue() {
        Filme filme = new Filme();
        assertTrue(filme.getDisponivel());
    }

    @Test
    void parameterizedConstructorShouldSetFields() {
        Filme filme = new Filme("Matrix", "Wachowski", 1999, "Sci-Fi");

        assertEquals("Matrix", filme.getTitulo());
        assertEquals("Wachowski", filme.getDiretor());
        assertEquals(1999, filme.getAno());
        assertEquals("Sci-Fi", filme.getGenero());
        assertTrue(filme.getDisponivel());
        assertNull(filme.getId());
    }

    @Test
    void settersShouldUpdateFields() {
        Filme filme = new Filme();
        filme.setId(1L);
        filme.setTitulo("Inception");
        filme.setDiretor("Nolan");
        filme.setAno(2010);
        filme.setGenero("Thriller");
        filme.setDisponivel(false);

        assertEquals(1L, filme.getId());
        assertEquals("Inception", filme.getTitulo());
        assertEquals("Nolan", filme.getDiretor());
        assertEquals(2010, filme.getAno());
        assertEquals("Thriller", filme.getGenero());
        assertFalse(filme.getDisponivel());
    }
}
