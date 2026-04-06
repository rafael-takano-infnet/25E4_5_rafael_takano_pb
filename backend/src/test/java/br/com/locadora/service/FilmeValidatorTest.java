package br.com.locadora.service;

import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmeValidatorTest {

    private final FilmeValidator filmeValidator = new FilmeValidator();

    @Test
    void shouldAcceptCurrentUpperLimit() {
        int maxYear = Year.now().getValue() + 5;

        assertDoesNotThrow(() -> filmeValidator.validateYear(maxYear));
    }

    @Test
    void shouldRejectYearAboveUpperLimit() {
        int invalidYear = Year.now().getValue() + 6;

        assertThrows(IllegalArgumentException.class, () -> filmeValidator.validateYear(invalidYear));
    }
}
