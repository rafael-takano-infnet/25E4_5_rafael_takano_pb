package br.com.locadora.service;

import br.com.locadora.exception.ResourceNotFoundException;
import br.com.locadora.model.Filme;
import br.com.locadora.repository.FilmeRepository;
import org.springframework.stereotype.Component;

@Component
public class FilmeFinder {

    private final FilmeRepository filmeRepository;

    public FilmeFinder(FilmeRepository filmeRepository) {
        this.filmeRepository = filmeRepository;
    }

    public Filme requireById(Long id) {
        return filmeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado com id: " + id));
    }
}
