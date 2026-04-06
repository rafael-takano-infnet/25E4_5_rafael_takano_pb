package br.com.locadora.service;

import br.com.locadora.dto.FilmeMapper;
import br.com.locadora.dto.FilmeRequestDTO;
import br.com.locadora.dto.FilmeResponseDTO;
import br.com.locadora.model.Filme;
import br.com.locadora.repository.FilmeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;
    private final FilmeFinder filmeFinder;
    private final FilmeValidator filmeValidator;
    private final FilmeMapper filmeMapper;

    public FilmeService(
            FilmeRepository filmeRepository,
            FilmeFinder filmeFinder,
            FilmeValidator filmeValidator,
            FilmeMapper filmeMapper
    ) {
        this.filmeRepository = filmeRepository;
        this.filmeFinder = filmeFinder;
        this.filmeValidator = filmeValidator;
        this.filmeMapper = filmeMapper;
    }

    public List<FilmeResponseDTO> findAll() {
        return filmeMapper.toResponseList(filmeRepository.findAll());
    }

    public FilmeResponseDTO findById(Long id) {
        return filmeMapper.toResponse(filmeFinder.requireById(id));
    }

    public FilmeResponseDTO create(FilmeRequestDTO dto) {
        filmeValidator.validateYear(dto.ano());
        Filme filme = filmeMapper.toEntity(dto);
        Filme saved = filmeRepository.save(filme);
        return filmeMapper.toResponse(saved);
    }

    public FilmeResponseDTO update(Long id, FilmeRequestDTO dto) {
        filmeValidator.validateYear(dto.ano());
        Filme filme = filmeFinder.requireById(id);
        filmeMapper.updateEntity(dto, filme);
        Filme saved = filmeRepository.save(filme);
        return filmeMapper.toResponse(saved);
    }

    public void delete(Long id) {
        filmeRepository.delete(filmeFinder.requireById(id));
    }
}
