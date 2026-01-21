package hotel.controller;

import hotel.model.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Klasa testów jednostkowych dla klasy RezerwacjeKontroler z użyciem Mockito.
 * Testy z symulacją (mockowaniem) zależności - Zadanie 2.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@DisplayName("Testy klasy RezerwacjeKontroler z Mockito")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestRezerwacjeKontrolerMock {
    
    @Mock
    private IHotelModel modelMock;
    
    @InjectMocks
    private RezerwacjeKontroler kontroler;
    
    private AutoCloseable mockitoCloseable;
    private Gosc gosc;
    private Pokoj pokoj;
    private LocalDate dataOd;
    private LocalDate dataDo;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Rozpoczęcie testów RezerwacjeKontroler z Mockito");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Zakończenie testów RezerwacjeKontroler z Mockito");
    }
    
    @BeforeEach
    void setUp() {
        // Jeśli: inicjalizacja mocków i danych testowych
        mockitoCloseable = MockitoAnnotations.openMocks(this);
        
        Gosc.resetIdCounter();
        gosc = new Gosc("Jan", "Kowalski", "jan@test.pl");
        pokoj = new Pokoj(101, "Dwuosobowy", 200.0);
        dataOd = LocalDate.of(2025, 6, 1);
        dataDo = LocalDate.of(2025, 6, 5);
    }
    
    @AfterEach
    void tearDown() throws Exception {
        // Wtedy: zamknięcie mocków
        mockitoCloseable.close();
    }
    
    // ========== TESTY UTWORZ REZERWACJE ==========
    
    @Test
    @Order(1)
    @DisplayName("utworzRezerwacje wywołuje model i zwraca rezerwację")
    @Tag("mock")
    @Tag("kontroler")
    void testUtworzRezerwacje_Sukces() {
        // Jeśli: mock modelu zwraca rezerwację
        Rezerwacja.resetIdCounter();
        Rezerwacja oczekiwanaRezerwacja = new Rezerwacja(dataOd, dataDo, gosc, pokoj);
        when(modelMock.utworzRezerwacje(gosc, pokoj, dataOd, dataDo))
            .thenReturn(oczekiwanaRezerwacja);
        
        // Gdy: kontroler tworzy rezerwację
        Rezerwacja wynik = kontroler.utworzRezerwacje(gosc, pokoj, dataOd, dataDo);
        
        // Wtedy: rezerwacja została utworzona
        assertNotNull(wynik, "Rezerwacja nie powinna być null");
        verify(modelMock, times(1)).utworzRezerwacje(gosc, pokoj, dataOd, dataDo);
    }
    
    @Test
    @Order(2)
    @DisplayName("utworzRezerwacje rzuca wyjątek dla null parametrów")
    @Tag("mock")
    @Tag("walidacja")
    void testUtworzRezerwacje_NullParametry() {
        // Jeśli: parametry są null
        // Gdy/Wtedy: kontroler rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> kontroler.utworzRezerwacje(null, pokoj, dataOd, dataDo),
            "Powinien zostać rzucony wyjątek dla null gościa"
        );
        
        // Wtedy: model nie został wywołany
        verify(modelMock, never()).utworzRezerwacje(any(), any(), any(), any());
    }
    
    @Test
    @Order(3)
    @DisplayName("utworzRezerwacje rzuca wyjątek gdy dataOd > dataDo")
    @Tag("mock")
    @Tag("walidacja")
    void testUtworzRezerwacje_NieprawidloweDaty() {
        // Jeśli: dataOd jest po dataDo
        LocalDate zlaDataOd = LocalDate.of(2025, 6, 10);
        LocalDate zlaDataDo = LocalDate.of(2025, 6, 5);
        
        // Gdy/Wtedy: kontroler rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> kontroler.utworzRezerwacje(gosc, pokoj, zlaDataOd, zlaDataDo),
            "Powinien zostać rzucony wyjątek dla nieprawidłowych dat"
        );
        
        verify(modelMock, never()).utworzRezerwacje(any(), any(), any(), any());
    }
    
    @Test
    @Order(4)
    @DisplayName("utworzRezerwacje zwraca null gdy model zwraca null")
    @Tag("mock")
    @Tag("kontroler")
    void testUtworzRezerwacje_ModelZwracaNull() {
        // Jeśli: mock modelu zwraca null (pokój niedostępny)
        when(modelMock.utworzRezerwacje(gosc, pokoj, dataOd, dataDo))
            .thenReturn(null);
        
        // Gdy: kontroler tworzy rezerwację
        Rezerwacja wynik = kontroler.utworzRezerwacje(gosc, pokoj, dataOd, dataDo);
        
        // Wtedy: zwrócony null
        assertNull(wynik, "Rezerwacja powinna być null gdy pokój niedostępny");
        verify(modelMock).utworzRezerwacje(gosc, pokoj, dataOd, dataDo);
    }
    
    // ========== TESTY ANULUJ REZERWACJE ==========
    
    @Test
    @Order(5)
    @DisplayName("anulujRezerwacje wywołuje model dla prawidłowego ID")
    @Tag("mock")
    @Tag("kontroler")
    void testAnulujRezerwacje_Sukces() {
        // Jeśli: mock modelu zwraca true
        int idRezerwacji = 1;
        when(modelMock.anulujRezerwacje(idRezerwacji)).thenReturn(true);
        
        // Gdy: kontroler anuluje rezerwację
        boolean wynik = kontroler.anulujRezerwacje(idRezerwacji);
        
        // Wtedy: anulowanie się powiodło
        assertTrue(wynik, "Anulowanie powinno się powieść");
        verify(modelMock, times(1)).anulujRezerwacje(idRezerwacji);
    }
    
    @Test
    @Order(6)
    @DisplayName("anulujRezerwacje zwraca false dla ID <= 0")
    @Tag("mock")
    @Tag("walidacja")
    void testAnulujRezerwacje_NieprawidloweId() {
        // Jeśli: ID jest nieprawidłowe
        // Gdy: kontroler próbuje anulować
        boolean wynik = kontroler.anulujRezerwacje(0);
        
        // Wtedy: zwrócone false bez wywołania modelu
        assertFalse(wynik, "Anulowanie powinno zwrócić false dla ID 0");
        verify(modelMock, never()).anulujRezerwacje(anyInt());
    }
    
    @Test
    @Order(7)
    @DisplayName("anulujRezerwacje zwraca false gdy model zwraca false")
    @Tag("mock")
    @Tag("kontroler")
    void testAnulujRezerwacje_ModelZwracaFalse() {
        // Jeśli: mock modelu zwraca false (rezerwacja nie istnieje)
        int idRezerwacji = 999;
        when(modelMock.anulujRezerwacje(idRezerwacji)).thenReturn(false);
        
        // Gdy: kontroler anuluje rezerwację
        boolean wynik = kontroler.anulujRezerwacje(idRezerwacji);
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Anulowanie powinno zwrócić false");
        verify(modelMock).anulujRezerwacje(idRezerwacji);
    }
    
    // ========== TESTY MODYFIKUJ REZERWACJE ==========
    
    @Test
    @Order(8)
    @DisplayName("modyfikujRezerwacje wywołuje model z nowymi datami")
    @Tag("mock")
    @Tag("kontroler")
    void testModyfikujRezerwacje_Sukces() {
        // Jeśli: mock modelu zwraca true
        int idRezerwacji = 1;
        LocalDate nowaDataOd = LocalDate.of(2025, 7, 1);
        LocalDate nowaDataDo = LocalDate.of(2025, 7, 10);
        when(modelMock.modyfikujRezerwacje(idRezerwacji, nowaDataOd, nowaDataDo))
            .thenReturn(true);
        
        // Gdy: kontroler modyfikuje rezerwację
        boolean wynik = kontroler.modyfikujRezerwacje(idRezerwacji, nowaDataOd, nowaDataDo);
        
        // Wtedy: modyfikacja się powiodła
        assertTrue(wynik, "Modyfikacja powinna się powieść");
        verify(modelMock).modyfikujRezerwacje(idRezerwacji, nowaDataOd, nowaDataDo);
    }
    
    @Test
    @Order(9)
    @DisplayName("modyfikujRezerwacje zwraca false dla nieprawidłowych dat")
    @Tag("mock")
    @Tag("walidacja")
    void testModyfikujRezerwacje_NieprawidloweDaty() {
        // Jeśli: nowa dataOd jest po dataDo
        int idRezerwacji = 1;
        LocalDate nowaDataOd = LocalDate.of(2025, 7, 15);
        LocalDate nowaDataDo = LocalDate.of(2025, 7, 10);
        
        // Gdy: kontroler próbuje modyfikować
        boolean wynik = kontroler.modyfikujRezerwacje(idRezerwacji, nowaDataOd, nowaDataDo);
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Modyfikacja powinna zwrócić false dla nieprawidłowych dat");
        verify(modelMock, never()).modyfikujRezerwacje(anyInt(), any(), any());
    }
    
    // ========== TESTY POBIERZ OPLATE ==========
    
    @Test
    @Order(10)
    @DisplayName("pobierzOplateZaAnulowanie wywołuje model")
    @Tag("mock")
    @Tag("kontroler")
    void testPobierzOplateZaAnulowanie_Sukces() {
        // Jeśli: mock modelu zwraca opłatę
        int idRezerwacji = 1;
        double oczekiwanaOplata = 160.0; // 20% z 800 zł
        when(modelMock.pobierzOplate(idRezerwacji)).thenReturn(oczekiwanaOplata);
        
        // Gdy: kontroler pobiera opłatę
        double wynik = kontroler.pobierzOplateZaAnulowanie(idRezerwacji);
        
        // Wtedy: zwrócona prawidłowa opłata
        assertEquals(oczekiwanaOplata, wynik, 0.01, "Opłata powinna być 160 zł");
        verify(modelMock).pobierzOplate(idRezerwacji);
    }
    
    @Test
    @Order(11)
    @DisplayName("pobierzOplateZaAnulowanie zwraca 0 dla ID <= 0")
    @Tag("mock")
    @Tag("walidacja")
    void testPobierzOplateZaAnulowanie_NieprawidloweId() {
        // Jeśli: ID jest nieprawidłowe
        // Gdy: kontroler pobiera opłatę
        double wynik = kontroler.pobierzOplateZaAnulowanie(-1);
        
        // Wtedy: zwrócone 0
        assertEquals(0, wynik, 0.01, "Opłata powinna być 0 dla nieprawidłowego ID");
        verify(modelMock, never()).pobierzOplate(anyInt());
    }
    
    // ========== TESTY WERYFIKACJI KOLEJNOŚCI I LICZBY WYWOŁAŃ ==========
    
    @Test
    @Order(12)
    @DisplayName("verify times sprawdza liczbę wywołań")
    @Tag("mock")
    @Tag("weryfikacja")
    void testVerifyTimes() {
        // Jeśli: model skonfigurowany
        when(modelMock.anulujRezerwacje(anyInt())).thenReturn(true);
        
        // Gdy: kontroler wywołuje anulowanie 3 razy
        kontroler.anulujRezerwacje(1);
        kontroler.anulujRezerwacje(2);
        kontroler.anulujRezerwacje(3);
        
        // Wtedy: model został wywołany dokładnie 3 razy
        verify(modelMock, times(3)).anulujRezerwacje(anyInt());
        verify(modelMock, atLeast(2)).anulujRezerwacje(anyInt());
        verify(modelMock, atMost(5)).anulujRezerwacje(anyInt());
    }
    
    @Test
    @Order(13)
    @DisplayName("verify never sprawdza brak wywołań")
    @Tag("mock")
    @Tag("weryfikacja")
    void testVerifyNever() {
        // Jeśli: nieprawidłowe ID
        // Gdy: kontroler próbuje anulować
        kontroler.anulujRezerwacje(0);
        
        // Wtedy: model nie został wywołany
        verify(modelMock, never()).anulujRezerwacje(anyInt());
    }
    
    @Test
    @Order(14)
    @DisplayName("InOrder weryfikuje kolejność wywołań")
    @Tag("mock")
    @Tag("weryfikacja")
    void testInOrder() {
        // Jeśli: model skonfigurowany
        Rezerwacja.resetIdCounter();
        Rezerwacja rezerwacja = new Rezerwacja(dataOd, dataDo, gosc, pokoj);
        when(modelMock.utworzRezerwacje(any(), any(), any(), any())).thenReturn(rezerwacja);
        when(modelMock.pobierzOplate(anyInt())).thenReturn(100.0);
        when(modelMock.anulujRezerwacje(anyInt())).thenReturn(true);
        
        // Gdy: kontroler wykonuje operacje w określonej kolejności
        kontroler.utworzRezerwacje(gosc, pokoj, dataOd, dataDo);
        kontroler.pobierzOplateZaAnulowanie(1);
        kontroler.anulujRezerwacje(1);
        
        // Wtedy: kolejność wywołań jest prawidłowa
        InOrder inOrder = inOrder(modelMock);
        inOrder.verify(modelMock).utworzRezerwacje(gosc, pokoj, dataOd, dataDo);
        inOrder.verify(modelMock).pobierzOplate(1);
        inOrder.verify(modelMock).anulujRezerwacje(1);
    }
    
    // ========== TESTY PARAMETRYZOWANE Z MOCK ==========
    
    @ParameterizedTest(name = "Test anulowania rezerwacji {0} - oczekiwany wynik: {1}")
    @Order(15)
    @DisplayName("Testy parametryzowane anulowania - CsvSource")
    @Tag("parametryzowany")
    @Tag("mock")
    @CsvSource({
        "1, true",
        "2, true",
        "999, false",
        "100, true"
    })
    void testAnulujRezerwacje_Parametryzowany(int idRezerwacji, boolean oczekiwanyWynik) {
        // Jeśli: mock skonfigurowany
        when(modelMock.anulujRezerwacje(idRezerwacji)).thenReturn(oczekiwanyWynik);
        
        // Gdy: kontroler anuluje rezerwację
        boolean wynik = kontroler.anulujRezerwacje(idRezerwacji);
        
        // Wtedy: wynik zgodny z oczekiwaniem
        assertEquals(oczekiwanyWynik, wynik);
        verify(modelMock).anulujRezerwacje(idRezerwacji);
    }
    
    // ========== TESTY WYJĄTKÓW ==========
    
    @Test
    @Order(16)
    @DisplayName("utworzRezerwacje obsługuje wyjątek z modelu")
    @Tag("mock")
    @Tag("wyjatek")
    void testUtworzRezerwacje_WyjatekZModelu() {
        // Jeśli: mock rzuca wyjątek
        when(modelMock.utworzRezerwacje(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Błąd bazy danych"));
        
        // Gdy/Wtedy: wyjątek jest propagowany
        assertThrows(
            RuntimeException.class,
            () -> kontroler.utworzRezerwacje(gosc, pokoj, dataOd, dataDo),
            "Wyjątek powinien być propagowany"
        );
    }

