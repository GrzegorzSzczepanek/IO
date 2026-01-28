package hotel.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasa testów jednostkowych dla klasy Gosc.
 * Testy operacji niezależnych - warstwa encji.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@DisplayName("Testy klasy Gosc")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestGosc {
    
    private Gosc gosc;
    
    @BeforeAll
    static void setUpBeforeClass() {
        // Jeśli: przygotowanie środowiska testowego
        Gosc.resetIdCounter();
        System.out.println("Rozpoczęcie testów klasy Gosc");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        // Wtedy: sprzątanie po testach
        System.out.println("Zakończenie testów klasy Gosc");
    }
    
    @BeforeEach
    void setUp() {
        // Jeśli: przygotowanie danych przed każdym testem
        Gosc.resetIdCounter();
        gosc = new Gosc("Jan", "Kowalski", "jan.kowalski@email.com");
    }
    
    @AfterEach
    void tearDown() {
        // Wtedy: sprzątanie po teście
        gosc = null;
    }
    
    // ========== TESTY KONSTRUKTORA ==========
    
    @Test
    @Order(1)
    @DisplayName("Konstruktor tworzy poprawny obiekt gościa")
    @Tag("konstruktor")
    @Tag("encja")
    void testKonstruktor_PoprawnyObiekt() {
        // Jeśli: podano prawidłowe dane
        String imie = "Anna";
        String nazwisko = "Nowak";
        String email = "anna.nowak@test.pl";
        
        // Gdy: tworzony jest nowy gość
        Gosc.resetIdCounter();
        Gosc nowyGosc = new Gosc(imie, nazwisko, email);
        
        // Wtedy: gość ma prawidłowe wartości
        assertAll("Sprawdzenie wszystkich atrybutów gościa",
            () -> assertEquals(1, nowyGosc.getId(), "ID powinno być 1"),
            () -> assertEquals(imie, nowyGosc.getImie(), "Imię powinno być 'Anna'"),
            () -> assertEquals(nazwisko, nowyGosc.getNazwisko(), "Nazwisko powinno być 'Nowak'"),
            () -> assertEquals(email, nowyGosc.getEmail(), "Email powinien być prawidłowy")
        );
    }
    
    @Test
    @Order(2)
    @DisplayName("Konstruktor z ID tworzy poprawny obiekt")
    @Tag("konstruktor")
    @Tag("encja")
    void testKonstruktorZId_PoprawnyObiekt() {
        // Jeśli: podano ID i dane
        int id = 100;
        String imie = "Piotr";
        String nazwisko = "Wiśniewski";
        String email = "piotr@test.pl";
        
        // Gdy: tworzony jest gość z określonym ID
        Gosc goscZId = new Gosc(id, imie, nazwisko, email);
        
        // Wtedy: gość ma prawidłowe ID
        assertEquals(id, goscZId.getId(), "ID powinno być 100");
    }
    
    @Test
    @Order(3)
    @DisplayName("Konstruktor rzuca wyjątek dla pustego imienia")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_PusteImie_RzucaWyjatek() {
        // Jeśli: imię jest puste
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Gosc("", "Kowalski", "test@test.pl"),
            "Powinien zostać rzucony wyjątek dla pustego imienia"
        );
    }
    
    @Test
    @Order(4)
    @DisplayName("Konstruktor rzuca wyjątek dla null imienia")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_NullImie_RzucaWyjatek() {
        // Jeśli: imię jest null
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Gosc(null, "Kowalski", "test@test.pl"),
            "Powinien zostać rzucony wyjątek dla null imienia"
        );
    }
    
    @Test
    @Order(5)
    @DisplayName("Konstruktor rzuca wyjątek dla nieprawidłowego emaila")
    @Tag("konstruktor")
    @Tag("walidacja")
    void testKonstruktor_NieprawidlowyEmail_RzucaWyjatek() {
        // Jeśli: email jest nieprawidłowy (bez @)
        String nieprawidlowyEmail = "nieprawidlowyemail.pl";
        
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Gosc("Jan", "Kowalski", nieprawidlowyEmail),
            "Powinien zostać rzucony wyjątek dla nieprawidłowego emaila"
        );
    }
    
    // ========== TESTY GETTERÓW ==========
    
    @Test
    @Order(6)
    @DisplayName("getImie zwraca prawidłowe imię")
    @Tag("getter")
    @Tag("encja")
    void testGetImie() {
        // Jeśli: gość ma imię "Jan"
        // Gdy: pobierane jest imię
        String imie = gosc.getImie();
        
        // Wtedy: zwrócone imię to "Jan"
        assertEquals("Janusz", imie, "Imię powinno być 'Jan'");
    }
    
    @Test
    @Order(7)
    @DisplayName("getNazwisko zwraca prawidłowe nazwisko")
    @Tag("getter")
    @Tag("encja")
    void testGetNazwisko() {
        // Jeśli: gość ma nazwisko "Kowalski"
        // Gdy: pobierane jest nazwisko
        String nazwisko = gosc.getNazwisko();
        
        // Wtedy: zwrócone nazwisko to "Kowalski"
        assertEquals("Kowalski", nazwisko, "Nazwisko powinno być 'Kowalski'");
    }
    
    @Test
    @Order(8)
    @DisplayName("getPelneNazwisko zwraca imię i nazwisko")
    @Tag("getter")
    @Tag("encja")
    void testGetPelneNazwisko() {
        // Jeśli: gość ma imię "Jan" i nazwisko "Kowalski"
        // Gdy: pobierane jest pełne nazwisko
        String pelneNazwisko = gosc.getPelneNazwisko();
        
        // Wtedy: zwrócone jest "Jan Kowalski"
        assertEquals("Jan Kowalski", pelneNazwisko, "Pełne nazwisko powinno być 'Jan Kowalski'");
    }
    
    @Test
    @Order(9)
    @DisplayName("getEmail zwraca prawidłowy email")
    @Tag("getter")
    @Tag("encja")
    void testGetEmail() {
        // Jeśli: gość ma określony email
        // Gdy: pobierany jest email
        String email = gosc.getEmail();
        
        // Wtedy: email zawiera @
        assertTrue(email.contains("@"), "Email powinien zawierać znak @");
        assertEquals("jan.kowalski@email.com", email);
    }
    
    // ========== TESTY SETTERA EMAIL ==========
    
    @Test
    @Order(10)
    @DisplayName("setEmail ustawia nowy email")
    @Tag("setter")
    @Tag("encja")
    void testSetEmail_PoprawnyEmail() {
        // Jeśli: gość ma email
        String nowyEmail = "nowy.email@test.pl";
        
        // Gdy: ustawiany jest nowy email
        gosc.setEmail(nowyEmail);
        
        // Wtedy: email został zmieniony
        assertEquals(nowyEmail, gosc.getEmail(), "Email powinien zostać zmieniony");
    }
    
    @Test
    @Order(11)
    @DisplayName("setEmail rzuca wyjątek dla nieprawidłowego emaila")
    @Tag("setter")
    @Tag("walidacja")
    void testSetEmail_NieprawidlowyEmail_RzucaWyjatek() {
        // Jeśli: próba ustawienia nieprawidłowego emaila
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> gosc.setEmail("bez-malpy"),
            "Powinien zostać rzucony wyjątek dla nieprawidłowego emaila"
        );
    }
    
    // ========== TESTY PARAMETRYZOWANE ==========
    
    @ParameterizedTest(name = "Test nieprawidłowego emaila: {0}")
    @Order(12)
    @DisplayName("Testy parametryzowane nieprawidłowych emaili - ValueSource")
    @Tag("parametryzowany")
    @Tag("walidacja")
    @ValueSource(strings = {"bezmalpy", "test.pl", "nazwa", "123456"})
    void testNieprawidloweEmaile_ValueSource(String nieprawidlowyEmail) {
        // Jeśli: podano nieprawidłowy email
        // Gdy/Wtedy: konstruktor rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Gosc("Jan", "Kowalski", nieprawidlowyEmail),
            "Powinien zostać rzucony wyjątek dla emaila: " + nieprawidlowyEmail
        );
    }
    
    @ParameterizedTest(name = "Test pustego/null imienia")
    @Order(13)
    @DisplayName("Testy parametryzowane pustych/null imion - NullAndEmptySource")
    @Tag("parametryzowany")
    @Tag("walidacja")
    @NullAndEmptySource
    void testPusteImiona_NullAndEmptySource(String imie) {
        // Jeśli: imię jest null lub puste
        // Gdy/Wtedy: konstruktor rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Gosc(imie, "Kowalski", "test@test.pl"),
            "Powinien zostać rzucony wyjątek dla imienia: " + imie
        );
    }
    
    /**
     * Źródło danych dla testu parametryzowanego - MethodSource.
     * Zwraca strumień prawidłowych kombinacji danych gościa.
     */
    static Stream<Object[]> dostawcaDanychGosci() {
        return Stream.of(
            new Object[]{"Adam", "Adamski", "adam@test.pl"},
            new Object[]{"Barbara", "Kowalska", "barbara@firma.com"},
            new Object[]{"Ćmilia", "Żółkiewska", "cmilia@test.pl"},
            new Object[]{"Jan", "O'Brien", "jan.obrien@test.com"}
        );
    }
    
    @ParameterizedTest(name = "Test gościa {0} {1}")
    @Order(14)
    @DisplayName("Testy parametryzowane prawidłowych danych - MethodSource")
    @Tag("parametryzowany")
    @Tag("encja")
    @MethodSource("dostawcaDanychGosci")
    void testPrawidloweDane_MethodSource(String imie, String nazwisko, String email) {
        // Jeśli: podano prawidłowe dane
        // Gdy: tworzony jest gość
        Gosc testGosc = new Gosc(imie, nazwisko, email);
        
        // Wtedy: gość ma prawidłowe dane
        assertAll(
            () -> assertEquals(imie, testGosc.getImie()),
            () -> assertEquals(nazwisko, testGosc.getNazwisko()),
            () -> assertEquals(email, testGosc.getEmail())
        );
    }
    
    // ========== TESTY EQUALS I HASHCODE ==========
    
    @Test
    @Order(15)
    @DisplayName("equals zwraca true dla gości o tym samym ID")
    @Tag("equals")
    @Tag("encja")
    void testEquals_TenSamId() {
        // Jeśli: dwóch gości ma to samo ID
        Gosc gosc1 = new Gosc(1, "Jan", "Kowalski", "jan@test.pl");
        Gosc gosc2 = new Gosc(1, "Anna", "Nowak", "anna@test.pl");
        
        // Gdy: porównywani są goście
        // Wtedy: są równi (bo mają to samo ID)
        assertEquals(gosc1, gosc2, "Goście o tym samym ID powinni być równi");
    }
    
    @Test
    @Order(16)
    @DisplayName("equals zwraca false dla gości o różnych ID")
    @Tag("equals")
    @Tag("encja")
    void testEquals_RozneId() {
        // Jeśli: dwóch gości ma różne ID
        Gosc gosc1 = new Gosc(1, "Jan", "Kowalski", "jan@test.pl");
        Gosc gosc2 = new Gosc(2, "Jan", "Kowalski", "jan@test.pl");
        
        // Gdy: porównywani są goście
        // Wtedy: nie są równi
        assertNotEquals(gosc1, gosc2, "Goście o różnych ID nie powinni być równi");
    }
    
    @Test
    @Order(17)
    @DisplayName("ID gości są kolejno generowane")
    @Tag("id")
    @Tag("encja")
    void testKolejneId() {
        // Jeśli: reset licznika ID
        Gosc.resetIdCounter();
        
        // Gdy: tworzone są kolejne obiekty
        Gosc g1 = new Gosc("A", "A", "a@a.pl");
        Gosc g2 = new Gosc("B", "B", "b@b.pl");
        Gosc g3 = new Gosc("C", "C", "c@c.pl");
        
        // Wtedy: ID są kolejne
        assertAll(
            () -> assertEquals(1, g1.getId()),
            () -> assertEquals(2, g2.getId()),
            () -> assertEquals(3, g3.getId())
        );
    }
    
    @Test
    @Order(18)
    @DisplayName("toString zawiera podstawowe informacje")
    @Tag("toString")
    @Tag("encja")
    void testToString() {
        // Jeśli: gość istnieje
        // Gdy: wywołane jest toString
        String opis = gosc.toString();
        
        // Wtedy: opis zawiera podstawowe informacje
        assertNotNull(opis);
        assertTrue(opis.contains("Jan"), "Opis powinien zawierać imię");
        assertTrue(opis.contains("Kowalski"), "Opis powinien zawierać nazwisko");
    }
}
