package br.com.locadora.repository;

import br.com.locadora.model.Filme;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FilmeRepositoryTest {

    @Autowired
    private FilmeRepository filmeRepository;

    @Test
    void shouldSaveAndFindById() {
        Filme filme = new Filme("Matrix", "Wachowski", 1999, "Sci-Fi");
        Filme saved = filmeRepository.save(filme);

        assertNotNull(saved.getId());

        Optional<Filme> found = filmeRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Matrix", found.get().getTitulo());
    }

    @Test
    void shouldFindAll() {
        filmeRepository.save(new Filme("Matrix", "Wachowski", 1999, "Sci-Fi"));
        filmeRepository.save(new Filme("Inception", "Nolan", 2010, "Thriller"));

        assertEquals(2, filmeRepository.findAll().size());
    }

    @Test
    void shouldDelete() {
        Filme filme = filmeRepository.save(new Filme("Matrix", "Wachowski", 1999, "Sci-Fi"));
        filmeRepository.delete(filme);

        assertFalse(filmeRepository.findById(filme.getId()).isPresent());
    }

    @Test
    void shouldUpdate() {
        Filme filme = filmeRepository.save(new Filme("Matrix", "Wachowski", 1999, "Sci-Fi"));
        filme.setTitulo("Matrix Reloaded");
        Filme updated = filmeRepository.save(filme);

        assertEquals("Matrix Reloaded", updated.getTitulo());
    }

    @Test
    void shouldReturnEmptyForNonExistentId() {
        Optional<Filme> found = filmeRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    void shouldPersistDisponivelDefault() {
        Filme filme = new Filme("Matrix", "Wachowski", 1999, "Sci-Fi");
        Filme saved = filmeRepository.save(filme);

        assertTrue(saved.getDisponivel());
    }
}
