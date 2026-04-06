package br.com.locadora.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FilmeRequestDTO(
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 255, message = "Título deve ter no máximo 255 caracteres")
        String titulo,

        @NotBlank(message = "Diretor é obrigatório")
        @Size(max = 255, message = "Diretor deve ter no máximo 255 caracteres")
        String diretor,

        @NotNull(message = "Ano é obrigatório")
        @Min(value = 1888, message = "Ano deve ser no mínimo 1888")
        Integer ano,

        @NotBlank(message = "Gênero é obrigatório")
        @Size(max = 100, message = "Gênero deve ter no máximo 100 caracteres")
        String genero,

        @NotNull(message = "Disponibilidade é obrigatória")
        Boolean disponivel
) {
}
