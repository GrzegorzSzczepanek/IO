package hotel.controller;

import hotel.model.*;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Klasa testów jednostkowych dla klasy ZameldowanieKontroler z użyciem Mockito.
 * Testy z symulacją (mockowaniem) zależności - Zadanie 2.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@DisplayName("Testy klasy ZameldowanieKontroler z Mockito")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestZameldowanieKontrolerMock {
    
    @Mock
    private IHotelModel modelMock;
    
    @InjectMocks
    private ZameldowanieKontroler kontroler;
    
    private AutoCloseable mockitoCloseable;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Rozpoczęcie testów ZameldowanieKontroler z Mockito");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Zakończenie testów ZameldowanieKontroler z Mockito");
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
    
    @Test
    @Order(1)
    @DisplayName("zameldujGoscia wywołuje model dla prawidłowego ID")
    @Tag("mock")
    @Tag("kontroler")
    @Tag("zameldowanie")
    void testZameldujGoscia_Sukces() {
        // Jeśli: mock modelu zwraca true
        int idRezerwacji = 1;
        when(modelMock.zameldujGoscia(idRezerwacji)).thenReturn(true);
        
        // Gdy: kontroler zameldowuje gościa
        boolean wynik = kontroler.zameldujGoscia(idRezerwacji);
        
        // Wtedy: zameldowanie się powiodło
        assertTrue(wynik, "Zameldowanie powinno się powieść");
        verify(modelMock, times(1)).zameldujGoscia(idRezerwacji);
    }
    
    @Test
    @Order(2)
    @DisplayName("zameldujGoscia zwraca false dla ID <= 0")
    @Tag("mock")
    @Tag("walidacja")
    @Tag("zameldowanie")
    void testZameldujGoscia_NieprawidloweId() {
        // Jeśli: ID jest nieprawidłowe
        // Gdy: kontroler próbuje zameldować
        boolean wynik = kontroler.zameldujGoscia(0);
        
        // Wtedy: zwrócone false bez wywołania modelu
        assertFalse(wynik, "Zameldowanie powinno zwrócić false dla ID 0");
        verify(modelMock, never()).zameldujGoscia(anyInt());
    }
    
    @Test
    @Order(3)
    @DisplayName("zameldujGoscia zwraca false gdy model zwraca false")
    @Tag("mock")
    @Tag("kontroler")
    @Tag("zameldowanie")
    void testZameldujGoscia_ModelZwracaFalse() {
        // Jeśli: mock modelu zwraca false
        int idRezerwacji = 999;
        when(modelMock.zameldujGoscia(idRezerwacji)).thenReturn(false);
        
        // Gdy: kontroler zameldowuje gościa
        boolean wynik = kontroler.zameldujGoscia(idRezerwacji);
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Zameldowanie powinno zwrócić false");
        verify(modelMock).zameldujGoscia(idRezerwacji);
    }
    
    @Test
    @Order(4)
    @DisplayName("zameldujGoscia zwraca false dla ujemnego ID")
    @Tag("mock")
    @Tag("walidacja")
    @Tag("zameldowanie")
    void testZameldujGoscia_UjemneId() {
        // Jeśli: ID jest ujemne
        // Gdy: kontroler próbuje zameldować
        boolean wynik = kontroler.zameldujGoscia(-5);
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Zameldowanie powinno zwrócić false dla ujemnego ID");
        verify(modelMock, never()).zameldujGoscia(anyInt());
    }
    
    @Test
    @Order(5)
    @DisplayName("Konstruktor rzuca wyjątek dla null modelu")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_NullModel() {
        // Jeśli: model jest null
        // Gdy/Wtedy: konstruktor rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new ZameldowanieKontroler(null),
            "Konstruktor powinien rzucić wyjątek dla null modelu"
        );
    }
}
