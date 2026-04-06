package br.com.locadora.controller;

import br.com.locadora.dto.FilmeRequestDTO;
import br.com.locadora.dto.FilmeResponseDTO;
import br.com.locadora.service.FilmeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/filmes")
public class FilmeController {

    private final FilmeService filmeService;

    public FilmeController(FilmeService filmeService) {
        this.filmeService = filmeService;
    }

    @GetMapping
    public ResponseEntity<List<FilmeResponseDTO>> findAll() {
        return ResponseEntity.ok(filmeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(filmeService.findById(id));
    }

    @PostMapping
    public ResponseEntity<FilmeResponseDTO> create(@Valid @RequestBody FilmeRequestDTO dto) {
        FilmeResponseDTO created = filmeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmeResponseDTO> update(@PathVariable Long id, @Valid @RequestBody FilmeRequestDTO dto) {
        return ResponseEntity.ok(filmeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        filmeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
