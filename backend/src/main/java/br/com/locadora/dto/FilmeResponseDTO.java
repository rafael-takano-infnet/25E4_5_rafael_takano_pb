package br.com.locadora.dto;

import br.com.locadora.model.Filme;

public record FilmeResponseDTO(
        Long id,
        String titulo,
        String diretor,
        Integer ano,
        String genero,
        Boolean disponivel
) {
    public FilmeResponseDTO(Filme filme) {
        this(
                filme.getId(),
                filme.getTitulo(),
                filme.getDiretor(),
                filme.getAno(),
                filme.getGenero(),
                filme.getDisponivel()
        );
    }
}
