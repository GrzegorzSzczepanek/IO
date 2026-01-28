package hotel.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasa testów jednostkowych dla klasy Pokoj.
 * Testy operacji niezależnych - warstwa encji.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@DisplayName("Testy klasy Pokoj")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestPokoj {
    
    private Pokoj pokoj;
    
    @BeforeAll
    static void setUpBeforeClass() {
        // Jeśli: przygotowanie środowiska testowego przed wszystkimi testami
        System.out.println("Rozpoczęcie testów klasy Pokoj");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        // Wtedy: sprzątanie po wszystkich testach
        System.out.println("Zakończenie testów klasy Pokoj");
    }
    
    @BeforeEach
    void setUp() {
        // Jeśli: przygotowanie danych przed każdym testem
        pokoj = new Pokoj(101, "Jednoosobowy", 150.0);
    }
    
    @AfterEach
    void tearDown() {
        // Wtedy: sprzątanie po każdym teście
        pokoj = null;
    }
    
    // ========== TESTY KONSTRUKTORA ==========
    
    @Test
    @Order(1)
    @DisplayName("Konstruktor tworzy poprawny obiekt pokoju")
    @Tag("konstruktor")
    @Tag("encja")
    void testKonstruktor_PoprawnyObiekt() {
        // Jeśli: podano prawidłowe parametry
        int numer = 102;
        String typ = "Dwuosobowy";
        double cena = 200.0;
        
        // Gdy: tworzony jest nowy pokój
        Pokoj nowyPokoj = new Pokoj(numer, typ, cena);
        
        // Wtedy: pokój ma prawidłowe wartości
        assertAll("Sprawdzenie wszystkich atrybutów pokoju",
            () -> assertEquals(numer, nowyPokoj.getNumer(), "Numer pokoju powinien być 102"),
            () -> assertEquals(typ, nowyPokoj.getTyp(), "Typ pokoju powinien być 'Dwuosobowy'"),
            () -> assertEquals(cena, nowyPokoj.getCenaBazowa(), 0.01, "Cena powinna być 200.0")
        );
    }
    
    @Test
    @Order(2)
    @DisplayName("Konstruktor rzuca wyjątek dla numeru <= 0")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_NumerZero_RzucaWyjatek() {
        // Jeśli: numer pokoju jest nieprawidłowy (0)
        int nieprawidlowyNumer = 0;
        
        // Gdy: próba utworzenia pokoju
        // Wtedy: rzucony zostaje wyjątek
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Pokoj(nieprawidlowyNumer, "Jednoosobowy", 100.0),
            "Powinien zostać rzucony wyjątek dla numeru 0"
        );
        
        assertTrue(exception.getMessage().contains("większy od 0"));
    }
    
    @Test
    @Order(3)
    @DisplayName("Konstruktor rzuca wyjątek dla pustego typu")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_PustyTyp_RzucaWyjatek() {
        // Jeśli: typ pokoju jest pusty
        String pustyTyp = "";
        
        // Gdy: próba utworzenia pokoju
        // Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Pokoj(101, pustyTyp, 100.0),
            "Powinien zostać rzucony wyjątek dla pustego typu"
        );
    }
    
    @Test
    @Order(4)
    @DisplayName("Konstruktor rzuca wyjątek dla ujemnej ceny")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_UjemnaCena_RzucaWyjatek() {
        // Jeśli: cena jest ujemna
        double ujemnaCena = -50.0;
        
        // Gdy: próba utworzenia pokoju
        // Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Pokoj(101, "Jednoosobowy", ujemnaCena),
            "Powinien zostać rzucony wyjątek dla ujemnej ceny"
        );
    }
    
    // ========== TESTY GETTERÓW ==========
    
    @Test
    @Order(5)
    @DisplayName("getNumer zwraca prawidłowy numer pokoju")
    @Tag("getter")
    @Tag("encja")
    void testGetNumer() {
        // Jeśli: pokój został utworzony z numerem 101
        // Gdy: pobierany jest numer
        int numer = pokoj.getNumer();
        
        // Wtedy: zwrócony numer to 101
        assertEquals(101, numer, "Numer pokoju powinien być 101");
    }
    
    @Test
    @Order(6)
    @DisplayName("getTyp zwraca prawidłowy typ pokoju")
    @Tag("getter")
    @Tag("encja")
    void testGetTyp() {
        // Jeśli: pokój został utworzony z typem "Jednoosobowy"
        // Gdy: pobierany jest typ
        String typ = pokoj.getTyp();
        
        // Wtedy: zwrócony typ to "Jednoosobowy"
        assertEquals("Jednoosobowy", typ, "Typ pokoju powinien być 'Jednoosobowy'");
    }
    
    @Test
    @Order(7)
    @DisplayName("getCenaBazowa zwraca prawidłową cenę")
    @Tag("getter")
    @Tag("encja")
    void testGetCenaBazowa() {
        // Jeśli: pokój został utworzony z ceną 150.0
        // Gdy: pobierana jest cena
        double cena = pokoj.getCenaBazowa();
        
        // Wtedy: zwrócona cena to 150.0
        assertEquals(150.0, cena, 0.01, "Cena bazowa powinna być 150.0");
    }
    
    // ========== TESTY SETTERA CENY ==========
    
    @Test
    @Order(8)
    @DisplayName("setCena ustawia nową cenę")
    @Tag("setter")
    @Tag("encja")
    void testSetCena_PoprawnaCena() {
        // Jeśli: pokój ma cenę 150.0
        // Gdy: ustawiana jest nowa cena
        pokoj.setCena(200.0);
        
        // Wtedy: cena została zmieniona
        assertEquals(200.0, pokoj.getCenaBazowa(), 0.01, "Cena powinna zostać zmieniona na 200.0");
    }
    
    @Test
    @Order(9)
    @DisplayName("setCena rzuca wyjątek dla ujemnej ceny")
    @Tag("setter")
    @Tag("walidacja")
    void testSetCena_UjemnaCena_RzucaWyjatek() {
        // Jeśli: próba ustawienia ujemnej ceny
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> pokoj.setCena(-100.0),
            "Powinien zostać rzucony wyjątek dla ujemnej ceny"
        );
    }
    
    // ========== TESTY PARAMETRYZOWANE ==========
    
    @ParameterizedTest(name = "Test pokoju typu {0} z ceną {1}")
    @Order(10)
    @DisplayName("Testy parametryzowane różnych typów pokoi - CsvSource")
    @Tag("parametryzowany")
    @Tag("encja")
    @CsvSource({
        "Jednoosobowy, 150.0",
        "Dwuosobowy, 250.0",
        "Apartament, 500.0",
        "Suite, 800.0"
    })
    void testRozneTypyPokoi_CsvSource(String typ, double cena) {
        // Jeśli: podano typ i cenę z parametrów
        // Gdy: tworzony jest pokój
        Pokoj testPokoj = new Pokoj(200, typ, cena);
        
        // Wtedy: pokój ma prawidłowe wartości
        assertAll(
            () -> assertEquals(typ, testPokoj.getTyp()),
            () -> assertEquals(cena, testPokoj.getCenaBazowa(), 0.01)
        );
    }
    
    @ParameterizedTest(name = "Test pokoju z numerem {0}")
    @Order(11)
    @DisplayName("Testy parametryzowane różnych numerów pokoi - ValueSource")
    @Tag("parametryzowany")
    @Tag("encja")
    @ValueSource(ints = {1, 10, 100, 101, 500, 999})
    void testRozneNumeryPokoi_ValueSource(int numer) {
        // Jeśli: podano numer z parametrów
        // Gdy: tworzony jest pokój
        Pokoj testPokoj = new Pokoj(numer, "Standard", 100.0);
        
        // Wtedy: pokój ma prawidłowy numer
        assertEquals(numer, testPokoj.getNumer(), "Numer pokoju powinien być " + numer);
    }
    
    // ========== TESTY EQUALS I HASHCODE ==========
    
    @Test
    @Order(12)
    @DisplayName("equals zwraca true dla pokoi o tym samym numerze")
    @Tag("equals")
    @Tag("encja")
    void testEquals_TenSamNumer() {
        // Jeśli: dwa pokoje mają ten sam numer
        Pokoj pokoj1 = new Pokoj(101, "Jednoosobowy", 150.0);
        Pokoj pokoj2 = new Pokoj(101, "Dwuosobowy", 250.0);
        
        // Gdy: porównywane są pokoje
        // Wtedy: są równe (bo mają ten sam numer)
        assertEquals(pokoj1, pokoj2, "Pokoje o tym samym numerze powinny być równe");
    }
    
    @Test
    @Order(13)
    @DisplayName("equals zwraca false dla pokoi o różnych numerach")
    @Tag("equals")
    @Tag("encja")
    void testEquals_RozneNumery() {
        // Jeśli: dwa pokoje mają różne numery
        Pokoj pokoj1 = new Pokoj(101, "Jednoosobowy", 150.0);
        Pokoj pokoj2 = new Pokoj(102, "Jednoosobowy", 150.0);
        
        // Gdy: porównywane są pokoje
        // Wtedy: nie są równe
        assertNotEquals(pokoj1, pokoj2, "Pokoje o różnych numerach nie powinny być równe");
    }
    
    @Test
    @Order(14)
    @DisplayName("toString zwraca niepusty opis")
    @Tag("toString")
    @Tag("encja")
    void testToString() {
        // Jeśli: pokój istnieje
        // Gdy: wywołane jest toString
        String opis = pokoj.toString();
        
        // Wtedy: opis nie jest pusty i zawiera podstawowe informacje
        assertNotNull(opis, "toString nie powinien zwracać null");
        assertTrue(opis.contains("101"), "Opis powinien zawierać numer pokoju");
        assertTrue(opis.contains("Jednoosobowy"), "Opis powinien zawierać typ pokoju");
    }
}
