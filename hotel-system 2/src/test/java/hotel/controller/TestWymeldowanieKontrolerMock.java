package hotel.controller;

import hotel.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Klasa testów jednostkowych dla klasy WymeldowanieKontroler z użyciem Mockito.
 * Testy z symulacją (mockowaniem) zależności - Zadanie 2.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@DisplayName("Testy klasy WymeldowanieKontroler z Mockito")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestWymeldowanieKontrolerMock {
    
    @Mock
    private IHotelModel modelMock;
    
    @InjectMocks
    private WymeldowanieKontroler kontroler;
    
    private AutoCloseable mockitoCloseable;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Rozpoczęcie testów WymeldowanieKontroler z Mockito");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Zakończenie testów WymeldowanieKontroler z Mockito");
    }
    
    @BeforeEach
    void setUp() {
        // Jeśli: inicjalizacja mocków
        mockitoCloseable = MockitoAnnotations.openMocks(this);
    }
    
    @AfterEach
    void tearDown() throws Exception {
        mockitoCloseable.close();
    }
    
    // ========== TESTY WYMELDUJ GOSCIA ==========
    
    @Test
    @Order(1)
    @DisplayName("wymeldujGoscia wywołuje model dla prawidłowego ID")
    @Tag("mock")
    @Tag("kontroler")
    @Tag("wymeldowanie")
    void testWymeldujGoscia_Sukces() {
        // Jeśli: mock modelu zwraca true
        int idRezerwacji = 1;
        when(modelMock.wymeldujGoscia(idRezerwacji)).thenReturn(true);
        
        // Gdy: kontroler wymeldowuje gościa
        boolean wynik = kontroler.wymeldujGoscia(idRezerwacji);
        
        // Wtedy: wymeldowanie się powiodło
        assertTrue(wynik, "Wymeldowanie powinno się powieść");
        verify(modelMock, times(1)).wymeldujGoscia(idRezerwacji);
    }
    
    @Test
    @Order(2)
    @DisplayName("wymeldujGoscia zwraca false dla ID <= 0")
    @Tag("mock")
    @Tag("walidacja")
    @Tag("wymeldowanie")
    void testWymeldujGoscia_NieprawidloweId() {
        // Jeśli: ID jest nieprawidłowe
        // Gdy: kontroler próbuje wymeldować
        boolean wynik = kontroler.wymeldujGoscia(0);
        
        // Wtedy: zwrócone false bez wywołania modelu
        assertFalse(wynik, "Wymeldowanie powinno zwrócić false dla ID 0");
        verify(modelMock, never()).wymeldujGoscia(anyInt());
    }
    
    @Test
    @Order(3)
    @DisplayName("wymeldujGoscia zwraca false gdy model zwraca false")
    @Tag("mock")
    @Tag("kontroler")
    @Tag("wymeldowanie")
    void testWymeldujGoscia_ModelZwracaFalse() {
        // Jeśli: mock modelu zwraca false
        int idRezerwacji = 999;
        when(modelMock.wymeldujGoscia(idRezerwacji)).thenReturn(false);
        
        // Gdy: kontroler wymeldowuje gościa
        boolean wynik = kontroler.wymeldujGoscia(idRezerwacji);
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Wymeldowanie powinno zwrócić false");
        verify(modelMock).wymeldujGoscia(idRezerwacji);
    }
    
    // ========== TESTY NALICZ OPLATE ZA POZNE WYMELDOWANIE ==========
    
    @Test
    @Order(4)
    @DisplayName("naliczOplateZaPozneWymeldowanie oblicza opłatę")
    @Tag("mock")
    @Tag("kontroler")
    @Tag("oplata")
    void testNaliczOplateZaPozneWymeldowanie_Sukces() {
        // Jeśli: mock modelu zwraca rezerwację
        int idRezerwacji = 1;
        int godzinyOpoznienia = 3;
        
        Gosc.resetIdCounter();
        Rezerwacja.resetIdCounter();
        Gosc gosc = new Gosc("Jan", "Kowalski", "jan@test.pl");
        Pokoj pokoj = new Pokoj(101, "Dwuosobowy", 200.0);
        Rezerwacja rezerwacja = new Rezerwacja(
            LocalDate.of(2025, 6, 1), 
            LocalDate.of(2025, 6, 5), 
            gosc, 
            pokoj
        );
        
        when(modelMock.znajdzRezerwacje(idRezerwacji)).thenReturn(Optional.of(rezerwacja));
        
        // Gdy: kontroler nalicza opłatę
        double wynik = kontroler.naliczOplateZaPozneWymeldowanie(idRezerwacji, godzinyOpoznienia);
        
        // Wtedy: opłata = 3 godziny * 25 zł = 75 zł
        assertEquals(75.0, wynik, 0.01, "Opłata powinna być 75 zł");
        verify(modelMock).znajdzRezerwacje(idRezerwacji);
    }
    
    @Test
    @Order(5)
    @DisplayName("naliczOplateZaPozneWymeldowanie zwraca 0 dla ID <= 0")
    @Tag("mock")
    @Tag("walidacja")
    @Tag("oplata")
    void testNaliczOplateZaPozneWymeldowanie_NieprawidloweId() {
        // Jeśli: ID jest nieprawidłowe
        // Gdy: kontroler nalicza opłatę
        double wynik = kontroler.naliczOplateZaPozneWymeldowanie(0, 3);
        
        // Wtedy: zwrócone 0
        assertEquals(0, wynik, 0.01, "Opłata powinna być 0 dla nieprawidłowego ID");
        verify(modelMock, never()).znajdzRezerwacje(anyInt());
    }
    
    @Test
    @Order(6)
    @DisplayName("naliczOplateZaPozneWymeldowanie zwraca 0 dla godzin <= 0")
    @Tag("mock")
    @Tag("walidacja")
    @Tag("oplata")
    void testNaliczOplateZaPozneWymeldowanie_NieprawidloweGodziny() {
        // Jeśli: liczba godzin jest nieprawidłowa
        // Gdy: kontroler nalicza opłatę
        double wynik = kontroler.naliczOplateZaPozneWymeldowanie(1, 0);
        
        // Wtedy: zwrócone 0
        assertEquals(0, wynik, 0.01, "Opłata powinna być 0 dla 0 godzin");
        verify(modelMock, never()).znajdzRezerwacje(anyInt());
    }
    
    @Test
    @Order(7)
    @DisplayName("naliczOplateZaPozneWymeldowanie zwraca 0 gdy rezerwacja nie istnieje")
    @Tag("mock")
    @Tag("kontroler")
    @Tag("oplata")
    void testNaliczOplateZaPozneWymeldowanie_BrakRezerwacji() {
        // Jeśli: mock modelu zwraca empty
        int idRezerwacji = 999;
        when(modelMock.znajdzRezerwacje(idRezerwacji)).thenReturn(Optional.empty());
        
        // Gdy: kontroler nalicza opłatę
        double wynik = kontroler.naliczOplateZaPozneWymeldowanie(idRezerwacji, 3);
        
        // Wtedy: zwrócone 0
        assertEquals(0, wynik, 0.01, "Opłata powinna być 0 dla nieistniejącej rezerwacji");
        verify(modelMock).znajdzRezerwacje(idRezerwacji);
    }
    
    // ========== TESTY PARAMETRYZOWANE ==========
    
    @ParameterizedTest(name = "Test opłaty za {0} godzin opóźnienia = {1} zł")
    @Order(8)
    @DisplayName("Testy parametryzowane naliczania opłaty - CsvSource")
    @Tag("parametryzowany")
    @Tag("mock")
    @Tag("oplata")
    @CsvSource({
        "1, 25.0",
        "2, 50.0",
        "4, 100.0",
        "6, 150.0",
        "10, 250.0"
    })
    void testNaliczOplateZaPozneWymeldowanie_Parametryzowany(int godziny, double oczekiwanaOplata) {
        // Jeśli: mock skonfigurowany
        int idRezerwacji = 1;
        Gosc.resetIdCounter();
        Rezerwacja.resetIdCounter();
        Gosc gosc = new Gosc("Test", "Test", "test@test.pl");
        Pokoj pokoj = new Pokoj(100, "Standard", 200.0);
        Rezerwacja rezerwacja = new Rezerwacja(
            LocalDate.of(2025, 6, 1), 
            LocalDate.of(2025, 6, 2), 
            gosc, 
            pokoj
        );
        
        when(modelMock.znajdzRezerwacje(idRezerwacji)).thenReturn(Optional.of(rezerwacja));
        
        // Gdy: kontroler nalicza opłatę
        double wynik = kontroler.naliczOplateZaPozneWymeldowanie(idRezerwacji, godziny);
        
        // Wtedy: opłata zgodna z oczekiwaniem
        assertEquals(oczekiwanaOplata, wynik, 0.01);
    }
    
    @Test
    @Order(9)
    @DisplayName("Konstruktor rzuca wyjątek dla null modelu")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_NullModel() {
        // Jeśli: model jest null
        // Gdy/Wtedy: konstruktor rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new WymeldowanieKontroler(null),
            "Konstruktor powinien rzucić wyjątek dla null modelu"
        );
    }
}
