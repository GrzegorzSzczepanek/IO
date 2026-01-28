package hotel.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasa testów jednostkowych dla klasy Rezerwacja.
 * Testy operacji zależnych od Gosc, Pokoj, IDodatek - warstwa encji.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@DisplayName("Testy klasy Rezerwacja")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestRezerwacja {
    
    private Rezerwacja rezerwacja;
    private Gosc gosc;
    private Pokoj pokoj;
    private LocalDate dataOd;
    private LocalDate dataDo;
    
    @BeforeAll
    static void setUpBeforeClass() {
        // Jeśli: przygotowanie środowiska
        Gosc.resetIdCounter();
        Rezerwacja.resetIdCounter();
        System.out.println("Rozpoczęcie testów klasy Rezerwacja");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Zakończenie testów klasy Rezerwacja");
    }
    
    @BeforeEach
    void setUp() {
        // Jeśli: przygotowanie danych przed testem
        Gosc.resetIdCounter();
        Rezerwacja.resetIdCounter();
        
        gosc = new Gosc("Jan", "Kowalski", "jan@test.pl");
        pokoj = new Pokoj(101, "Dwuosobowy", 200.0);
        dataOd = LocalDate.of(2025, 6, 1);
        dataDo = LocalDate.of(2025, 6, 5);
        
        rezerwacja = new Rezerwacja(dataOd, dataDo, gosc, pokoj);
    }
    
    @AfterEach
    void tearDown() {
        rezerwacja = null;
        gosc = null;
        pokoj = null;
    }
    
    // ========== TESTY KONSTRUKTORA ==========
    
    @Test
    @Order(1)
    @DisplayName("Konstruktor tworzy poprawny obiekt rezerwacji")
    @Tag("konstruktor")
    @Tag("encja")
    void testKonstruktor_PoprawnyObiekt() {
        // Jeśli: podano prawidłowe parametry
        // Gdy: rezerwacja została utworzona w setUp
        // Wtedy: rezerwacja ma prawidłowe wartości
        assertAll("Sprawdzenie atrybutów rezerwacji",
            () -> assertEquals(1, rezerwacja.getId(), "ID powinno być 1"),
            () -> assertEquals(dataOd, rezerwacja.getDataOd()),
            () -> assertEquals(dataDo, rezerwacja.getDataDo()),
            () -> assertEquals(gosc, rezerwacja.getGosc()),
            () -> assertEquals(pokoj, rezerwacja.getPokoj()),
            () -> assertEquals(Rezerwacja.Status.NOWA, rezerwacja.getStatus())
        );
    }
    
    @Test
    @Order(2)
    @DisplayName("Konstruktor rzuca wyjątek gdy dataOd > dataDo")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_DataOdPoDatDo_RzucaWyjatek() {
        // Jeśli: dataOd jest po dataDo
        LocalDate zlaDataOd = LocalDate.of(2025, 6, 10);
        LocalDate zlaDataDo = LocalDate.of(2025, 6, 5);
        
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Rezerwacja(zlaDataOd, zlaDataDo, gosc, pokoj),
            "Powinien zostać rzucony wyjątek gdy dataOd > dataDo"
        );
    }
    
    @Test
    @Order(3)
    @DisplayName("Konstruktor rzuca wyjątek gdy dataOd == dataDo")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_TeSameDaty_RzucaWyjatek() {
        // Jeśli: dataOd == dataDo
        LocalDate data = LocalDate.of(2025, 6, 5);
        
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Rezerwacja(data, data, gosc, pokoj),
            "Rezerwacja musi trwać co najmniej jeden dzień"
        );
    }
    
    @Test
    @Order(4)
    @DisplayName("Konstruktor rzuca wyjątek dla null gościa")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_NullGosc_RzucaWyjatek() {
        // Jeśli: gość jest null
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Rezerwacja(dataOd, dataDo, null, pokoj),
            "Gość nie może być null"
        );
    }
    
    @Test
    @Order(5)
    @DisplayName("Konstruktor rzuca wyjątek dla null pokoju")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_NullPokoj_RzucaWyjatek() {
        // Jeśli: pokój jest null
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Rezerwacja(dataOd, dataDo, gosc, null),
            "Pokój nie może być null"
        );
    }
    
    // ========== TESTY OBLICZANIA CENY ==========
    
    @Test
    @Order(6)
    @DisplayName("obliczCene zwraca prawidłową cenę bazową")
    @Tag("obliczenia")
    @Tag("encja")
    void testObliczCene_BezDodatkow() {
        // Jeśli: rezerwacja na 4 noce, pokój 200 zł/noc, bez dodatków
        // Gdy: obliczana jest cena
        double cena = rezerwacja.obliczCene();
        
        // Wtedy: cena = 4 * 200 = 800
        assertEquals(800.0, cena, 0.01, "Cena powinna być 800 zł (4 noce x 200 zł)");
    }
    
    @Test
    @Order(7)
    @DisplayName("getLiczbaNocy zwraca prawidłową liczbę nocy")
    @Tag("obliczenia")
    @Tag("encja")
    void testGetLiczbaNocy() {
        // Jeśli: rezerwacja od 1.06 do 5.06
        // Gdy: pobierana jest liczba nocy
        long liczbaNocy = rezerwacja.getLiczbaNocy();
        
        // Wtedy: liczba nocy = 4
        assertEquals(4, liczbaNocy, "Liczba nocy powinna być 4");
    }
    
    @Test
    @Order(8)
    @DisplayName("obliczCene uwzględnia dodatki")
    @Tag("obliczenia")
    @Tag("encja")
    void testObliczCene_ZDodatkami() {
        // Jeśli: rezerwacja z dodatkami
        Sniadanie sniadanie = new Sniadanie(50.0, 4); // 4 dni x 50 zł = 200 zł
        Parking parking = new Parking(30.0, 4); // 4 dni x 30 zł = 120 zł
        
        // Gdy: dodawane są dodatki
        rezerwacja.dodajDodatek(sniadanie);
        rezerwacja.dodajDodatek(parking);
        double cena = rezerwacja.obliczCene();
        
        // Wtedy: cena = 800 + 200 + 120 = 1120
        assertEquals(1120.0, cena, 0.01, "Cena powinna być 1120 zł");
    }
    
    // ========== TESTY DODATKÓW ==========
    
    @Test
    @Order(9)
    @DisplayName("dodajDodatek dodaje dodatek do rezerwacji")
    @Tag("dodatki")
    @Tag("encja")
    void testDodajDodatek_Sukces() {
        // Jeśli: rezerwacja bez dodatków
        Sniadanie sniadanie = new Sniadanie(4);
        
        // Gdy: dodawany jest dodatek
        boolean wynik = rezerwacja.dodajDodatek(sniadanie);
        
        // Wtedy: dodatek został dodany
        assertTrue(wynik, "Dodanie dodatku powinno się powieść");
        assertTrue(rezerwacja.maDodatki(), "Rezerwacja powinna mieć dodatki");
        assertEquals(1, rezerwacja.pobierzDodatki().size());
    }
    
    @Test
    @Order(10)
    @DisplayName("dodajDodatek zwraca false dla null")
    @Tag("dodatki")
    @Tag("walidacja")
    void testDodajDodatek_Null() {
        // Jeśli: próba dodania null
        // Gdy: wywoływane jest dodajDodatek
        boolean wynik = rezerwacja.dodajDodatek(null);
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Dodanie null powinno zwrócić false");
    }
    
    @Test
    @Order(11)
    @DisplayName("pobierzDodatki zwraca niemodyfikowalną listę")
    @Tag("dodatki")
    @Tag("encja")
    void testPobierzDodatki_NiemodyfikowalnaLista() {
        // Jeśli: rezerwacja z dodatkiem
        rezerwacja.dodajDodatek(new Sniadanie(4));
        
        // Gdy: pobierana jest lista dodatków
        List<IDodatek> dodatki = rezerwacja.pobierzDodatki();
        
        // Wtedy: próba modyfikacji rzuca wyjątek
        assertThrows(
            UnsupportedOperationException.class,
            () -> dodatki.add(new Parking(4)),
            "Lista powinna być niemodyfikowalna"
        );
    }
    
    @Test
    @Order(12)
    @DisplayName("usunDodatek usuwa dodatek z rezerwacji")
    @Tag("dodatki")
    @Tag("encja")
    void testUsunDodatek() {
        // Jeśli: rezerwacja z dodatkiem
        Sniadanie sniadanie = new Sniadanie(4);
        rezerwacja.dodajDodatek(sniadanie);
        
        // Gdy: usuwany jest dodatek
        boolean wynik = rezerwacja.usunDodatek(sniadanie);
        
        // Wtedy: dodatek został usunięty
        assertTrue(wynik, "Usunięcie dodatku powinno się powieść");
        assertFalse(rezerwacja.maDodatki(), "Rezerwacja nie powinna mieć dodatków");
    }
    
    // ========== TESTY ZMIANY STATUSU ==========
    
    @Test
    @Order(13)
    @DisplayName("anuluj zmienia status na ANULOWANA")
    @Tag("status")
    @Tag("encja")
    void testAnuluj_Sukces() {
        // Jeśli: rezerwacja ma status NOWA
        // Gdy: anulowana jest rezerwacja
        boolean wynik = rezerwacja.anuluj();
        
        // Wtedy: status zmieniony na ANULOWANA
        assertTrue(wynik, "Anulowanie powinno się powieść");
        assertEquals(Rezerwacja.Status.ANULOWANA, rezerwacja.getStatus());
    }
    
    @Test
    @Order(14)
    @DisplayName("anuluj zwraca false dla już anulowanej rezerwacji")
    @Tag("status")
    @Tag("walidacja")
    void testAnuluj_JuzAnulowana() {
        // Jeśli: rezerwacja jest już anulowana
        rezerwacja.anuluj();

        // Gdy: ponowna próba anulowania
        boolean wynik = rezerwacja.anuluj();

        // Wtedy: zwrócone false
        assertFalse(wynik, "Ponowne anulowanie powinno zwrócić false");
    }

    @Test
    @Order(17)
    @DisplayName("anuluj z przyczyną zmienia status na ANULOWANA i ustawia przyczynę")
    @Tag("status")
    @Tag("encja")
    void testAnulujZPrzyczyna_Sukces() {
        // Jeśli: rezerwacja ma status NOWA
        String przyczyna = "Zmiana planów";

        // Gdy: anulowana jest rezerwacja z przyczyną
        boolean wynik = rezerwacja.anuluj(przyczyna);

        // Wtedy: status zmieniony na ANULOWANA i przyczyna ustawiona
        assertTrue(wynik, "Anulowanie z przyczyną powinno się powieść");
        assertEquals(Rezerwacja.Status.ANULOWANA, rezerwacja.getStatus());
        assertEquals(przyczyna, rezerwacja.getPrzyczynaAnulowania());
    }

    @Test
    @Order(18)
    @DisplayName("anuluj z przyczyną zwraca false dla już anulowanej rezerwacji")
    @Tag("status")
    @Tag("walidacja")
    void testAnulujZPrzyczyna_JuzAnulowana() {
        // Jeśli: rezerwacja jest już anulowana
        String przyczyna1 = "Choroba";
        String przyczyna2 = "Zmiana planów";
        rezerwacja.anuluj(przyczyna1);

        // Gdy: ponowna próba anulowania z inną przyczyną
        boolean wynik = rezerwacja.anuluj(przyczyna2);

        // Wtedy: zwrócone false i pierwotna przyczyna zachowana
        assertFalse(wynik, "Ponowne anulowanie powinno zwrócić false");
        assertEquals(przyczyna1, rezerwacja.getPrzyczynaAnulowania());
    }

    @Test
    @Order(19)
    @DisplayName("dodajDodatek zwraca false dla anulowanej rezerwacji")
    @Tag("dodatki")
    @Tag("status")
    void testDodajDodatek_AnulowanaRezerwacja() {
        // Jeśli: rezerwacja jest anulowana
        rezerwacja.anuluj();
        
        // Gdy: próba dodania dodatku
        boolean wynik = rezerwacja.dodajDodatek(new Sniadanie(4));
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Nie można dodać dodatku do anulowanej rezerwacji");
    }
    
    // ========== TESTY ZMIANY DAT ==========
    
    @Test
    @Order(16)
    @DisplayName("zmienDaty zmienia daty rezerwacji")
    @Tag("daty")
    @Tag("encja")
    void testZmienDaty_Sukces() {
        // Jeśli: rezerwacja ma określone daty
        LocalDate nowaDataOd = LocalDate.of(2025, 7, 1);
        LocalDate nowaDataDo = LocalDate.of(2025, 7, 10);
        
        // Gdy: zmieniane są daty
        rezerwacja.zmienDaty(nowaDataOd, nowaDataDo);
        
        // Wtedy: daty zostały zmienione
        assertAll(
            () -> assertEquals(nowaDataOd, rezerwacja.getDataOd()),
            () -> assertEquals(nowaDataDo, rezerwacja.getDataDo()),
            () -> assertEquals(9, rezerwacja.getLiczbaNocy())
        );
    }
    
    @Test
    @Order(17)
    @DisplayName("zmienDaty rzuca wyjątek dla anulowanej rezerwacji")
    @Tag("daty")
    @Tag("status")
    void testZmienDaty_AnulowanaRezerwacja() {
        // Jeśli: rezerwacja jest anulowana
        rezerwacja.anuluj();
        
        // Gdy/Wtedy: próba zmiany dat rzuca wyjątek
        assertThrows(
            IllegalStateException.class,
            () -> rezerwacja.zmienDaty(LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 5)),
            "Nie można zmienić dat anulowanej rezerwacji"
        );
    }
    
    // ========== TESTY PARAMETRYZOWANE ==========
    
    /**
     * Źródło danych dla testu różnych długości rezerwacji.
     */
    static Stream<Arguments> dostawcaDlugosciRezerwacji() {
        return Stream.of(
            Arguments.of(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 2), 1, 200.0),
            Arguments.of(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 8), 7, 1400.0),
            Arguments.of(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 15), 14, 2800.0),
            Arguments.of(LocalDate.of(2025, 6, 1), LocalDate.of(2025, 7, 1), 30, 6000.0)
        );
    }
    
    @ParameterizedTest(name = "Rezerwacja {2} nocy = {3} zł")
    @Order(18)
    @DisplayName("Testy parametryzowane różnych długości rezerwacji - MethodSource")
    @Tag("parametryzowany")
    @Tag("obliczenia")
    @MethodSource("dostawcaDlugosciRezerwacji")
    void testRozneDlugosciRezerwacji_MethodSource(
            LocalDate dataOd, LocalDate dataDo, long oczekiwanaLiczbaNocy, double oczekiwanaCena) {
        // Jeśli: podano daty rezerwacji
        Gosc.resetIdCounter();
        Rezerwacja.resetIdCounter();
        Gosc testGosc = new Gosc("Test", "Test", "test@test.pl");
        Pokoj testPokoj = new Pokoj(100, "Standard", 200.0);
        
        // Gdy: tworzona jest rezerwacja
        Rezerwacja testRez = new Rezerwacja(dataOd, dataDo, testGosc, testPokoj);
        
        // Wtedy: liczba nocy i cena są prawidłowe
        assertAll(
            () -> assertEquals(oczekiwanaLiczbaNocy, testRez.getLiczbaNocy()),
            () -> assertEquals(oczekiwanaCena, testRez.obliczCene(), 0.01)
        );
    }
    
    @Test
    @Order(19)
    @DisplayName("equals porównuje po ID")
    @Tag("equals")
    @Tag("encja")
    void testEquals() {
        // Jeśli: dwie rezerwacje o tym samym ID
        Rezerwacja.resetIdCounter();
        Rezerwacja rez1 = new Rezerwacja(1, dataOd, dataDo, gosc, pokoj);
        Rezerwacja rez2 = new Rezerwacja(1, dataOd.plusDays(10), dataDo.plusDays(10), gosc, pokoj);
        
        // Gdy: porównywane są rezerwacje
        // Wtedy: są równe
        assertEquals(rez1, rez2, "Rezerwacje o tym samym ID powinny być równe");
    }
    
    @Test
    @Order(20)
    @DisplayName("toString zawiera podstawowe informacje")
    @Tag("toString")
    @Tag("encja")
    void testToString() {
        // Jeśli: rezerwacja istnieje
        // Gdy: wywołane jest toString
        String opis = rezerwacja.toString();
        
        // Wtedy: opis zawiera informacje
        assertAll(
            () -> assertNotNull(opis),
            () -> assertTrue(opis.contains("Jan Kowalski")),
            () -> assertTrue(opis.contains("101"))
        );
    }
}
