package hotel.controller;

import hotel.model.Gosc;
import hotel.model.IHotelModel;
import hotel.model.Pokoj;
import hotel.model.Rezerwacja;
import hotel.strategy.IStrategiaAnulowaniaRezerwacji;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe RezerwacjeKontroler z użyciem Mockito.
 * Zadanie 2: Testy z mockowaniem zależności.
 */
@DisplayName("Testy RezerwacjeKontroler z Mockito")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
@Tag("mockito")
@Tag("kontroler")
class TestRezerwacjeKontrolerMockito {
    
    @Mock
    private IHotelModel model;
    
    @Mock
    private IStrategiaAnulowaniaRezerwacji strategia;
    
    private RezerwacjeKontroler kontroler;
    
    private Rezerwacja testRezerwacja;
    private Gosc testGosc;
    private Pokoj testPokoj;
    
    @BeforeEach
    void setUp() {
        kontroler = new RezerwacjeKontroler(model, strategia);
        
        testGosc = new Gosc(1, "Jan", "Kowalski", "jan@test.pl");
        testPokoj = new Pokoj(101, "dwuosobowy", 200.0);
        testRezerwacja = new Rezerwacja(1,
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(14),
            testPokoj, testGosc);
    }
    
    @AfterEach
    void tearDown() {
        testRezerwacja = null;
    }
    
    // ========== TESTY ANULOWANIA ==========
    
    @Test
    @Order(1)
    @DisplayName("anulujRezerwacje deleguje do modelu gdy strategia pozwala")
    void anulujRezerwacje_DelegujeDomodeluGdyStrategiaPozwala() {
        when(model.pobierzRezerwacje(1)).thenReturn(testRezerwacja);
        when(strategia.czyMoznaAnulowac(testRezerwacja)).thenReturn(true);
        when(model.anulujRezerwacje(1)).thenReturn(true);
        
        boolean wynik = kontroler.anulujRezerwacje(1);
        
        assertAll(
            () -> assertTrue(wynik),
            () -> verify(model).pobierzRezerwacje(1),
            () -> verify(strategia).czyMoznaAnulowac(testRezerwacja),
            () -> verify(model).anulujRezerwacje(1)
        );
    }
    
    @Test
    @Order(2)
    @DisplayName("anulujRezerwacje nie wywołuje modelu gdy strategia nie pozwala")
    void anulujRezerwacje_NieWywolujeModeluGdyStrategiaNiePozwala() {
        when(model.pobierzRezerwacje(1)).thenReturn(testRezerwacja);
        when(strategia.czyMoznaAnulowac(testRezerwacja)).thenReturn(false);
        
        boolean wynik = kontroler.anulujRezerwacje(1);
        
        assertAll(
            () -> assertFalse(wynik),
            () -> verify(model, never()).anulujRezerwacje(anyInt())
        );
    }
    
    @Test
    @Order(3)
    @DisplayName("anulujRezerwacje zwraca false dla nieistniejącej rezerwacji")
    void anulujRezerwacje_ZwracaFalseDlaNieistniejacej() {
        when(model.pobierzRezerwacje(999)).thenReturn(null);
        
        boolean wynik = kontroler.anulujRezerwacje(999);
        
        assertFalse(wynik);
        verify(strategia, never()).czyMoznaAnulowac(any());
    }
    
    // ========== TESTY OBLICZANIA KARY ==========
    
    @Test
    @Order(4)
    @DisplayName("pobierzOplateZaAnulowanie oblicza 20% ceny")
    void pobierzOplateZaAnulowanie_Oblicza20Procent() {
        when(model.pobierzOplate(1)).thenReturn(1000.0);
        
        double kara = kontroler.pobierzOplateZaAnulowanie(1);
        
        assertEquals(200.0, kara, 0.01);
        verify(model).pobierzOplate(1);
    }
    
    @ParameterizedTest(name = "20% z {0} = {1}")
    @Order(5)
    @DisplayName("Parametryzowane testy opłaty 20%")
    @CsvSource({
        "100.0, 20.0",
        "500.0, 100.0",
        "1000.0, 200.0",
        "2500.0, 500.0"
    })
    void pobierzOplateZaAnulowanie_RozneCeny(double cena, double oczekiwanaKara) {
        when(model.pobierzOplate(anyInt())).thenReturn(cena);
        
        double kara = kontroler.pobierzOplateZaAnulowanie(1);
        
        assertEquals(oczekiwanaKara, kara, 0.01);
    }
    
    @Test
    @Order(6)
    @DisplayName("obliczOplateZaAnulowanieZTerminem używa strategii")
    void obliczOplateZaAnulowanieZTerminem_UzywaStrategii() {
        when(model.pobierzRezerwacje(1)).thenReturn(testRezerwacja);
        when(strategia.obliczKareZaAnulowanie(testRezerwacja)).thenReturn(400.0);
        
        double kara = kontroler.obliczOplateZaAnulowanieZTerminem(1);
        
        assertEquals(400.0, kara, 0.01);
        verify(strategia).obliczKareZaAnulowanie(testRezerwacja);
    }
    
    @Test
    @Order(7)
    @DisplayName("obliczOplateZaAnulowanieZTerminem rzuca wyjątek dla nieistniejącej")
    void obliczOplateZaAnulowanieZTerminem_RzucaWyjatekDlaNieistniejacej() {
        when(model.pobierzRezerwacje(999)).thenReturn(null);
        
        assertThrows(IllegalArgumentException.class,
            () -> kontroler.obliczOplateZaAnulowanieZTerminem(999));
    }
    
    // ========== TESTY TWORZENIA REZERWACJI ==========
    
    @Test
    @Order(8)
    @DisplayName("utworzRezerwacje deleguje do modelu")
    void utworzRezerwacje_DelegujeDomodeluModelu() {
        LocalDate dataOd = LocalDate.now().plusDays(5);
        LocalDate dataDo = LocalDate.now().plusDays(10);
        
        when(model.utworzRezerwacje(1, 101, dataOd, dataDo)).thenReturn(testRezerwacja);
        
        Rezerwacja wynik = kontroler.utworzRezerwacje(1, 101, dataOd, dataDo);
        
        assertNotNull(wynik);
        verify(model).utworzRezerwacje(1, 101, dataOd, dataDo);
    }
    
    // ========== TESTY WERYFIKACJI WYWOŁAŃ ==========
    
    @ParameterizedTest(name = "Anulowanie rezerwacji ID={0}")
    @Order(9)
    @DisplayName("Weryfikacja wywołań dla różnych ID")
    @ValueSource(ints = {1, 5, 10, 100})
    void weryfikacjaWywolanDlaRoznychId(int id) {
        when(model.pobierzRezerwacje(id)).thenReturn(testRezerwacja);
        when(strategia.czyMoznaAnulowac(testRezerwacja)).thenReturn(true);
        when(model.anulujRezerwacje(id)).thenReturn(true);
        
        kontroler.anulujRezerwacje(id);
        
        verify(model).pobierzRezerwacje(id);
        verify(model).anulujRezerwacje(id);
    }
    
    @Test
    @Order(10)
    @DisplayName("Weryfikacja kolejności wywołań")
    void weryfikacjaKolejnosciWywolanWywolanWywolan() {
        when(model.pobierzRezerwacje(1)).thenReturn(testRezerwacja);
        when(strategia.czyMoznaAnulowac(testRezerwacja)).thenReturn(true);
        when(model.anulujRezerwacje(1)).thenReturn(true);
        
        kontroler.anulujRezerwacje(1);
        
        var inOrder = inOrder(model, strategia);
        inOrder.verify(model).pobierzRezerwacje(1);
        inOrder.verify(strategia).czyMoznaAnulowac(testRezerwacja);
        inOrder.verify(model).anulujRezerwacje(1);
    }
}
