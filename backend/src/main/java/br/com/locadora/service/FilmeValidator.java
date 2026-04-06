package br.com.locadora.service;

import org.springframework.stereotype.Component;

import java.time.Year;

@Component
public class FilmeValidator {

    public void validateYear(Integer ano) {
        int maxYear = Year.now().getValue() + 5;
        if (ano > maxYear) {
            throw new IllegalArgumentException("Ano não pode ser superior a " + maxYear);
        }
    }
}
