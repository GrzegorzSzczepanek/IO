package hotel.suite;

import org.junit.platform.suite.api.*;

/**
 * Zestaw testów parametryzowanych.
 * Zawiera testy oznaczone tagiem "parametryzowany".
 * Praktyczne zastosowanie: weryfikacja poprawności dla różnych kombinacji danych.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@Suite
@SuiteDisplayName("Zestaw testów parametryzowanych")
@SelectPackages({"hotel.model", "hotel.controller", "hotel.dao"})
@IncludeTags("parametryzowany")
public class SuiteTestyParametryzowane {
    // Klasa zestawu testów parametryzowanych
    // Praktyczne zastosowanie: testowanie z wieloma kombinacjami danych wejściowych
    // Użyteczne do testów regresyjnych i sprawdzania edge cases
}
