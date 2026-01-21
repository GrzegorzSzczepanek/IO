package hotel.usecase;

import hotel.controller.RezerwacjeKontroler;
import hotel.dao.GoscieDAO;
import hotel.dao.PokojeDAO;
import hotel.dao.RezerwacjeDAO;
import hotel.factory.FabrykaGosci;
import hotel.model.*;
import hotel.strategy.AnulowaniePrzezGoscia;
import hotel.strategy.AnulowaniePrzezRecepcje;
import hotel.strategy.IStrategiaAnulowaniaRezerwacji;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy integracyjne dla przypadku użycia "Anuluj Rezerwację".
 * Testuje cały przepływ: UseCase -> Controller -> Model -> DAO -> Entity
 * 
 * Zadanie 1: Testy bez mockowania (integracyjne)
 */
@DisplayName("Testy integracyjne: Anuluj Rezerwację - cały przepływ")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integracyjne")
@Tag("anulowanie")
class TestAnulujRezerwacjeIntegracyjne {
    
    private GoscieDAO goscieDAO;
    private PokojeDAO pokojeDAO;
    private RezerwacjeDAO rezerwacjeDAO;
    private HotelModel model;
    private RezerwacjeKontroler kontroler;
    private AnulujRezerwacje useCase;
    
    private Gosc testGosc;
    private Pokoj testPokoj;
    private Rezerwacja testRezerwacja;
    
    @BeforeEach
    void setUp() {
        Gosc.resetIdCounter();
        
        goscieDAO = new GoscieDAO();
        pokojeDAO = new PokojeDAO();
        rezerwacjeDAO = new RezerwacjeDAO();
        
        model = new HotelModel(rezerwacjeDAO, pokojeDAO, goscieDAO, new FabrykaGosci());
        kontroler = new RezerwacjeKontroler(model);
        useCase = new AnulujRezerwacje(kontroler);
        
        testGosc = new Gosc("Jan", "Kowalski", "jan@test.pl");
        goscieDAO.zapisz(testGosc);
        
        testPokoj = new Pokoj(101, "dwuosobowy", 200.0);
        pokojeDAO.zapisz(testPokoj);
        
        testRezerwacja = new Rezerwacja(1,
            LocalDate.now().plusDays(10),
            LocalDate.now().plusDays(14),
            testPokoj, testGosc);
        rezerwacjeDAO.zapisz(testRezerwacja);
    }
    
    @AfterEach
    void tearDown() {
        goscieDAO.wyczyscWszystko();
        pokojeDAO.wyczyscWszystko();
        rezerwacjeDAO.wyczyscWszystko();
    }
    
    @Test
    @Order(1)
    @DisplayName("Anulowanie nowej rezerwacji powinno się powieść")
    void anulowanie_NowejRezerwacji_PowinnoSiePowiesc() {
        assertEquals(Rezerwacja.StatusRezerwacji.NOWA, testRezerwacja.getStatus());
        
        AnulujRezerwacje.WynikAnulowania wynik = useCase.wykonaj(1);
        
        assertAll("Wynik anulowania",
            () -> assertTrue(wynik.isSukces()),
            () -> assertNotNull(wynik.getKomunikat()),
            () -> assertEquals(Rezerwacja.StatusRezerwacji.ANULOWANA, 
                              rezerwacjeDAO.pobierz(1).getStatus())
        );
    }
    
    @Test
    @Order(2)
    @DisplayName("Anulowanie nieistniejącej rezerwacji powinno się nie powieść")
    void anulowanie_NieistniejacejRezerwacji_NiePowinnoSiePowiesc() {
        AnulujRezerwacje.WynikAnulowania wynik = useCase.wykonaj(999);
        
        assertAll(
            () -> assertFalse(wynik.isSukces()),
            () -> assertTrue(wynik.getKomunikat().contains("nie istnieje"))
        );
    }
    
    @Test
    @Order(3)
    @DisplayName("Anulowanie zameldowanej rezerwacji powinno się nie powieść")
    void anulowanie_ZameldowanejRezerwacji_NiePowinnoSiePowiesc() {
        testRezerwacja.setStatus(Rezerwacja.StatusRezerwacji.ZAMELDOWANA);
        rezerwacjeDAO.aktualizuj(testRezerwacja);
        
        AnulujRezerwacje.WynikAnulowania wynik = useCase.wykonaj(1);
        
        assertFalse(wynik.isSukces());
    }
    
    @ParameterizedTest(name = "Anulowanie rezerwacji o statusie {0}")
    @Order(4)
    @DisplayName("Testy anulowania dla różnych statusów")
    @EnumSource(Rezerwacja.StatusRezerwacji.class)
    void anulowanie_RozneStatusy(Rezerwacja.StatusRezerwacji status) {
        testRezerwacja.setStatus(status);
        rezerwacjeDAO.aktualizuj(testRezerwacja);
        
        AnulujRezerwacje.WynikAnulowania wynik = useCase.wykonaj(1);
        
        boolean oczekiwanySukces = (status == Rezerwacja.StatusRezerwacji.NOWA || 
                                    status == Rezerwacja.StatusRezerwacji.POTWIERDZONA);
        assertEquals(oczekiwanySukces, wynik.isSukces());
    }
    
    @ParameterizedTest(name = "Kara za anulowanie {0} dni przed: {1}%")
    @Order(5)
    @DisplayName("Testy kar za anulowanie według terminu")
    @CsvSource({
        "10, 0.0",
        "5, 0.20",
        "2, 0.50",
        "0, 1.00"
    })
    void karyZaAnulowanie_WedlugTerminu(int dniDoRezerwacji, double oczekiwanyProcent) {
        Rezerwacja rezerwacja = new Rezerwacja(2,
            LocalDate.now().plusDays(dniDoRezerwacji),
            LocalDate.now().plusDays(dniDoRezerwacji + 4),
            testPokoj, testGosc);
        rezerwacjeDAO.zapisz(rezerwacja);
        
        double cenaRezerwacji = rezerwacja.obliczCene();
        double oczekiwanaKara = cenaRezerwacji * oczekiwanyProcent;
        
        double faktycznaKara = kontroler.obliczOplateZaAnulowanieZTerminem(2);
        
        assertEquals(oczekiwanaKara, faktycznaKara, 0.01);
    }
    
    @Test
    @Order(6)
    @DisplayName("Strategia recepcji pozwala anulować zameldowaną rezerwację")
    void strategiaRecepcji_PozwalaAnulowacZameldowana() {
        RezerwacjeKontroler kontrolerRecepcji = new RezerwacjeKontroler(
            model, new AnulowaniePrzezRecepcje());
        AnulujRezerwacje useCaseRecepcji = new AnulujRezerwacje(kontrolerRecepcji);
        
        testRezerwacja.setStatus(Rezerwacja.StatusRezerwacji.ZAMELDOWANA);
        rezerwacjeDAO.aktualizuj(testRezerwacja);
        
        AnulujRezerwacje.WynikAnulowania wynik = useCaseRecepcji.wykonaj(1);
        
        assertTrue(wynik.isSukces());
        assertEquals(0.0, wynik.getOplataPobrana(), 0.01);
    }
    
    @Test
    @Order(7)
    @DisplayName("Pełny przepływ: utworzenie -> potwierdzenie -> anulowanie")
    void pelnyPrzeplyw_UtworzeniePotwierdzienieAnulowanie() {
        Rezerwacja nowaRezerwacja = kontroler.utworzRezerwacje(
            testGosc.getId(), testPokoj.getNumer(),
            LocalDate.now().plusDays(20), LocalDate.now().plusDays(25));
        
        model.potwierdzPlatnosc(nowaRezerwacja.getId());
        assertEquals(Rezerwacja.StatusRezerwacji.POTWIERDZONA, 
                    rezerwacjeDAO.pobierz(nowaRezerwacja.getId()).getStatus());
        
        AnulujRezerwacje.WynikAnulowania wynik = useCase.wykonaj(nowaRezerwacja.getId());
        
        assertAll("Pełny przepływ",
            () -> assertTrue(wynik.isSukces()),
            () -> assertEquals(0.0, wynik.getOplataPobrana(), 0.01),
            () -> assertEquals(Rezerwacja.StatusRezerwacji.ANULOWANA,
                              rezerwacjeDAO.pobierz(nowaRezerwacja.getId()).getStatus())
        );
    }
    
    @Test
    @Order(8)
    @DisplayName("Po anulowaniu pokój jest ponownie dostępny")
    void poAnulowaniu_PokojDostepny() {
        assertFalse(rezerwacjeDAO.czyPokojDostepny(101, 
            testRezerwacja.getDataOd(), testRezerwacja.getDataDo()));
        
        useCase.wykonaj(1);
        
        assertTrue(rezerwacjeDAO.czyPokojDostepny(101, 
            testRezerwacja.getDataOd(), testRezerwacja.getDataDo()));
    }
}
