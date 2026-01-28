package hotel.suite;

import org.junit.platform.suite.api.*;

/**
 * Zestaw testów walidacji danych wejściowych.
 * Zawiera testy oznaczone tagiem "walidacja" z wykluczeniem testów "mock".
 * Praktyczne zastosowanie: szybkie sprawdzenie poprawności walidacji.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@Suite
@SuiteDisplayName("Zestaw testów walidacji danych")
@SelectPackages({"hotel.model", "hotel.controller", "hotel.dao"})
@IncludeTags("walidacja")
@ExcludeTags("mock")
public class SuiteTestyWalidacji {
    // Klasa zestawu testów walidacji
    // Praktyczne zastosowanie: weryfikacja poprawności walidacji parametrów
    // bez uruchamiania testów z mockowaniem
}
