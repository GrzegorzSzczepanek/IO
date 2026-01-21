package hotel.suite;

import org.junit.platform.suite.api.*;

/**
 * Zestaw testów z użyciem Mockito.
 * Zawiera testy oznaczone tagiem "mock".
 * Praktyczne zastosowanie: testowanie integracji między warstwami z symulacją zależności.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */

@Suite
@SuiteDisplayName("Zestaw testów z mockowaniem (Mockito)")
@SelectPackages({"hotel.controller"})
@IncludeTags("mock")
@ExcludeTags("walidacja")
public class SuiteTestyMock {
    // Klasa zestawu testów z mockowaniem
    // Praktyczne zastosowanie: testowanie zachowania kontrolerów
    // z symulacją modelu bez konieczności tworzenia rzeczywistych obiektów
}
