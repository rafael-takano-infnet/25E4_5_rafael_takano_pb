package br.com.locadora.dto;

import br.com.locadora.model.Filme;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilmeMapper {

    public Filme toEntity(FilmeRequestDTO dto) {
        Filme filme = new Filme();
        updateEntity(dto, filme);
        return filme;
    }

    public void updateEntity(FilmeRequestDTO dto, Filme filme) {
        filme.setTitulo(dto.titulo());
        filme.setDiretor(dto.diretor());
        filme.setAno(dto.ano());
        filme.setGenero(dto.genero());
        filme.setDisponivel(dto.disponivel());
    }

    public FilmeResponseDTO toResponse(Filme filme) {
        return new FilmeResponseDTO(
                filme.getId(),
                filme.getTitulo(),
                filme.getDiretor(),
                filme.getAno(),
                filme.getGenero(),
                filme.getDisponivel()
        );
    }

    public List<FilmeResponseDTO> toResponseList(List<Filme> filmes) {
        return filmes.stream()
                .map(this::toResponse)
                .toList();
    }
}
