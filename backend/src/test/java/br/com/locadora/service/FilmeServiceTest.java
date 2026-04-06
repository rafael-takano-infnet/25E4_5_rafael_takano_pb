package br.com.locadora.service;

import br.com.locadora.dto.FilmeRequestDTO;
import br.com.locadora.dto.FilmeResponseDTO;
import br.com.locadora.dto.FilmeMapper;
import br.com.locadora.exception.ResourceNotFoundException;
import br.com.locadora.model.Filme;
import br.com.locadora.repository.FilmeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmeServiceTest {

    @Mock
    private FilmeRepository filmeRepository;

    @Mock
    private FilmeFinder filmeFinder;

    @Mock
    private FilmeValidator filmeValidator;

    @Mock
    private FilmeMapper filmeMapper;

    @InjectMocks
    private FilmeService filmeService;

    private Filme sampleFilme;
    private FilmeRequestDTO sampleRequest;

    @BeforeEach
    void setUp() {
        sampleFilme = new Filme("Matrix", "Wachowski", 1999, "Sci-Fi");
        sampleFilme.setId(1L);

        sampleRequest = new FilmeRequestDTO("Matrix", "Wachowski", 1999, "Sci-Fi", true);
    }

    @Test
    void findAllShouldReturnList() {
        when(filmeRepository.findAll()).thenReturn(List.of(sampleFilme));
        when(filmeMapper.toResponseList(List.of(sampleFilme))).thenReturn(List.of(new FilmeResponseDTO(sampleFilme)));

        List<FilmeResponseDTO> result = filmeService.findAll();

        assertEquals(1, result.size());
        assertEquals("Matrix", result.get(0).titulo());
        verify(filmeRepository).findAll();
    }

    @Test
    void findAllShouldReturnEmptyList() {
        when(filmeRepository.findAll()).thenReturn(List.of());
        when(filmeMapper.toResponseList(List.of())).thenReturn(List.of());

        List<FilmeResponseDTO> result = filmeService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdShouldReturnFilme() {
        when(filmeFinder.requireById(1L)).thenReturn(sampleFilme);
        when(filmeMapper.toResponse(sampleFilme)).thenReturn(new FilmeResponseDTO(sampleFilme));

        FilmeResponseDTO result = filmeService.findById(1L);

        assertEquals("Matrix", result.titulo());
        assertEquals(1L, result.id());
    }

    @Test
    void findByIdShouldThrowWhenNotFound() {
        when(filmeFinder.requireById(99L)).thenThrow(new ResourceNotFoundException("Filme não encontrado com id: 99"));

        assertThrows(ResourceNotFoundException.class, () -> filmeService.findById(99L));
    }

    @Test
    void createShouldSaveAndReturnFilme() {
        Filme mappedFilme = new Filme("Matrix", "Wachowski", 1999, "Sci-Fi");
        when(filmeMapper.toEntity(sampleRequest)).thenReturn(mappedFilme);
        when(filmeRepository.save(any(Filme.class))).thenReturn(sampleFilme);
        when(filmeMapper.toResponse(sampleFilme)).thenReturn(new FilmeResponseDTO(sampleFilme));

        FilmeResponseDTO result = filmeService.create(sampleRequest);

        assertEquals("Matrix", result.titulo());
        verify(filmeValidator).validateYear(sampleRequest.ano());
        verify(filmeRepository).save(any(Filme.class));
    }

    @Test
    void updateShouldModifyAndReturnFilme() {
        when(filmeFinder.requireById(1L)).thenReturn(sampleFilme);
        when(filmeRepository.save(any(Filme.class))).thenReturn(sampleFilme);
        when(filmeMapper.toResponse(sampleFilme)).thenReturn(new FilmeResponseDTO(sampleFilme));

        FilmeRequestDTO updateRequest = new FilmeRequestDTO("Matrix Reloaded", "Wachowski", 2003, "Sci-Fi", true);
        FilmeResponseDTO result = filmeService.update(1L, updateRequest);

        assertNotNull(result);
        verify(filmeValidator).validateYear(updateRequest.ano());
        verify(filmeMapper).updateEntity(updateRequest, sampleFilme);
        verify(filmeRepository).save(any(Filme.class));
    }

    @Test
    void updateShouldThrowWhenNotFound() {
        when(filmeFinder.requireById(99L)).thenThrow(new ResourceNotFoundException("Filme não encontrado com id: 99"));

        assertThrows(ResourceNotFoundException.class, () -> filmeService.update(99L, sampleRequest));
    }

    @Test
    void deleteShouldRemoveFilme() {
        when(filmeFinder.requireById(1L)).thenReturn(sampleFilme);

        filmeService.delete(1L);

        verify(filmeRepository).delete(sampleFilme);
    }

    @Test
    void deleteShouldThrowWhenNotFound() {
        when(filmeFinder.requireById(99L)).thenThrow(new ResourceNotFoundException("Filme não encontrado com id: 99"));

        assertThrows(ResourceNotFoundException.class, () -> filmeService.delete(99L));
    }

    @ParameterizedTest
    @CsvSource({
            "1888, true",
            "2000, true",
            "2025, true",
            "2031, true",
            "2032, false",
            "9999, false"
    })
    void yearValidationShouldEnforce(int year, boolean shouldSucceed) {
        FilmeRequestDTO request = new FilmeRequestDTO("Test", "Director", year, "Genre", true);

        if (shouldSucceed) {
            when(filmeMapper.toEntity(request)).thenReturn(sampleFilme);
            when(filmeRepository.save(any(Filme.class))).thenReturn(sampleFilme);
            when(filmeMapper.toResponse(sampleFilme)).thenReturn(new FilmeResponseDTO(sampleFilme));
            assertDoesNotThrow(() -> filmeService.create(request));
            verify(filmeValidator).validateYear(year);
        } else {
            doThrow(new IllegalArgumentException("Ano inválido")).when(filmeValidator).validateYear(year);
            assertThrows(IllegalArgumentException.class, () -> filmeService.create(request));
        }
    }

    @Test
    void createShouldSetDisponivelFromDto() {
        FilmeRequestDTO request = new FilmeRequestDTO("Test", "Director", 2020, "Genre", false);
        Filme savedFilme = new Filme("Test", "Director", 2020, "Genre");
        savedFilme.setDisponivel(false);
        savedFilme.setId(2L);

        when(filmeMapper.toEntity(request)).thenReturn(savedFilme);
        when(filmeRepository.save(any(Filme.class))).thenReturn(savedFilme);
        when(filmeMapper.toResponse(savedFilme)).thenReturn(new FilmeResponseDTO(savedFilme));

        FilmeResponseDTO result = filmeService.create(request);

        assertFalse(result.disponivel());
    }
}
