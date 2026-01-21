package hotel.dao;

import hotel.model.Gosc;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasa testów jednostkowych dla klasy GoscieDAO.
 * Testy operacji warstwy dostępu do danych (DAO).
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@DisplayName("Testy klasy GoscieDAO")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestGoscieDAO {
    
    private GoscieDAO goscieDAO;
    private Gosc gosc1;
    private Gosc gosc2;
    
    @BeforeAll
    static void setUpBeforeClass() {
        Gosc.resetIdCounter();
        System.out.println("Rozpoczęcie testów klasy GoscieDAO");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Zakończenie testów klasy GoscieDAO");
    }
    
    @BeforeEach
    void setUp() {
        // Jeśli: przygotowanie DAO i danych testowych
        Gosc.resetIdCounter();
        goscieDAO = new GoscieDAO();
        gosc1 = new Gosc("Jan", "Kowalski", "jan@test.pl");
        gosc2 = new Gosc("Anna", "Nowak", "anna@test.pl");
    }
    
    @AfterEach
    void tearDown() {
        // Wtedy: czyszczenie po teście
        goscieDAO.wyczysc();
        goscieDAO = null;
    }
    
    // ========== TESTY ZAPISZ ==========
    
    @Test
    @Order(1)
    @DisplayName("zapisz dodaje gościa do bazy")
    @Tag("dao")
    @Tag("zapisz")
    void testZapisz_NowyGosc() {
        // Jeśli: gość nie istnieje w bazie
        // Gdy: zapisywany jest gość
        Gosc zapisany = goscieDAO.zapisz(gosc1);
        
        // Wtedy: gość został zapisany
        assertNotNull(zapisany, "Zapisany gość nie powinien być null");
        assertEquals(1, goscieDAO.liczba(), "Powinien być 1 gość w bazie");
    }
    
    @Test
    @Order(2)
    @DisplayName("zapisz rzuca wyjątek dla null")
    @Tag("dao")
    @Tag("walidacja")
    void testZapisz_Null_RzucaWyjatek() {
        // Jeśli: próba zapisania null
        // Gdy/Wtedy: rzucony zostaje wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> goscieDAO.zapisz(null),
            "Zapisanie null powinno rzucić wyjątek"
        );
    }
    
    // ========== TESTY POBIERZ ==========
    
    @Test
    @Order(3)
    @DisplayName("pobierz zwraca gościa po ID")
    @Tag("dao")
    @Tag("pobierz")
    void testPobierz_IstniejacyGosc() {
        // Jeśli: gość jest zapisany
        goscieDAO.zapisz(gosc1);
        
        // Gdy: pobierany jest gość po ID
        Optional<Gosc> wynik = goscieDAO.pobierz(gosc1.getId());
        
        // Wtedy: gość został znaleziony
        assertTrue(wynik.isPresent(), "Gość powinien zostać znaleziony");
        assertEquals(gosc1.getEmail(), wynik.get().getEmail());
    }
    
    @Test
    @Order(4)
    @DisplayName("pobierz zwraca empty dla nieistniejącego ID")
    @Tag("dao")
    @Tag("pobierz")
    void testPobierz_NieistniejacyGosc() {
        // Jeśli: baza jest pusta
        // Gdy: pobierany jest gość po ID
        Optional<Gosc> wynik = goscieDAO.pobierz(999);
        
        // Wtedy: zwrócony empty
        assertTrue(wynik.isEmpty(), "Powinien zostać zwrócony empty Optional");
    }
    
    // ========== TESTY POBIERZ WSZYSTKIE ==========
    
    @Test
    @Order(5)
    @DisplayName("pobierzWszystkie zwraca wszystkich gości")
    @Tag("dao")
    @Tag("pobierz")
    void testPobierzWszystkie() {
        // Jeśli: zapisano kilku gości
        goscieDAO.zapisz(gosc1);
        goscieDAO.zapisz(gosc2);
        
        // Gdy: pobierani są wszyscy
        List<Gosc> wszyscy = goscieDAO.pobierzWszystkie();
        
        // Wtedy: lista zawiera wszystkich gości
        assertEquals(2, wszyscy.size(), "Powinno być 2 gości");
    }
    
    @Test
    @Order(6)
    @DisplayName("pobierzWszystkie zwraca pustą listę gdy brak gości")
    @Tag("dao")
    @Tag("pobierz")
    void testPobierzWszystkie_PustaBaza() {
        // Jeśli: baza jest pusta
        // Gdy: pobierani są wszyscy
        List<Gosc> wszyscy = goscieDAO.pobierzWszystkie();
        
        // Wtedy: lista jest pusta
        assertTrue(wszyscy.isEmpty(), "Lista powinna być pusta");
    }
    
    // ========== TESTY USUN ==========
    
    @Test
    @Order(7)
    @DisplayName("usun usuwa gościa z bazy")
    @Tag("dao")
    @Tag("usun")
    void testUsun_IstniejacyGosc() {
        // Jeśli: gość jest zapisany
        goscieDAO.zapisz(gosc1);
        
        // Gdy: usuwany jest gość
        boolean wynik = goscieDAO.usun(gosc1.getId());
        
        // Wtedy: gość został usunięty
        assertTrue(wynik, "Usunięcie powinno się powieść");
        assertEquals(0, goscieDAO.liczba(), "Baza powinna być pusta");
    }
    
    @Test
    @Order(8)
    @DisplayName("usun zwraca false dla nieistniejącego ID")
    @Tag("dao")
    @Tag("usun")
    void testUsun_NieistniejacyGosc() {
        // Jeśli: gość nie istnieje
        // Gdy: próba usunięcia
        boolean wynik = goscieDAO.usun(999);
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Usunięcie nieistniejącego powinno zwrócić false");
    }
    
    // ========== TESTY AKTUALIZUJ ==========
    
    @Test
    @Order(9)
    @DisplayName("aktualizuj aktualizuje dane gościa")
    @Tag("dao")
    @Tag("aktualizuj")
    void testAktualizuj_IstniejacyGosc() {
        // Jeśli: gość jest zapisany
        goscieDAO.zapisz(gosc1);
        gosc1.setEmail("nowy.email@test.pl");
        
        // Gdy: aktualizowany jest gość
        boolean wynik = goscieDAO.aktualizuj(gosc1);
        
        // Wtedy: dane zostały zaktualizowane
        assertTrue(wynik, "Aktualizacja powinna się powieść");
        Optional<Gosc> pobrany = goscieDAO.pobierz(gosc1.getId());
        assertEquals("nowy.email@test.pl", pobrany.get().getEmail());
    }
    
    @Test
    @Order(10)
    @DisplayName("aktualizuj zwraca false dla null")
    @Tag("dao")
    @Tag("walidacja")
    void testAktualizuj_Null() {
        // Jeśli: próba aktualizacji null
        // Gdy: wywoływane jest aktualizuj
        boolean wynik = goscieDAO.aktualizuj(null);
        
        // Wtedy: zwrócone false
        assertFalse(wynik, "Aktualizacja null powinna zwrócić false");
    }
    
    // ========== TESTY WYSZUKIWANIA ==========
    
    @Test
    @Order(11)
    @DisplayName("znajdzPoEmail znajduje gościa")
    @Tag("dao")
    @Tag("wyszukiwanie")
    void testZnajdzPoEmail() {
        // Jeśli: gość jest zapisany
        goscieDAO.zapisz(gosc1);
        
        // Gdy: wyszukiwany jest gość po emailu
        Optional<Gosc> wynik = goscieDAO.znajdzPoEmail("jan@test.pl");
        
        // Wtedy: gość został znaleziony
        assertTrue(wynik.isPresent());
        assertEquals("Jan", wynik.get().getImie());
    }
    
    @Test
    @Order(12)
    @DisplayName("znajdzPoNazwisku znajduje gości")
    @Tag("dao")
    @Tag("wyszukiwanie")
    void testZnajdzPoNazwisku() {
        // Jeśli: zapisano gości
        goscieDAO.zapisz(gosc1);
        Gosc.resetIdCounter();
        Gosc gosc3 = new Gosc(10, "Piotr", "Kowalski", "piotr@test.pl");
        goscieDAO.zapisz(gosc3);
        
        // Gdy: wyszukiwani są goście po nazwisku
        List<Gosc> wynik = goscieDAO.znajdzPoNazwisku("Kowalski");
        
        // Wtedy: znaleziono 2 gości
        assertEquals(2, wynik.size(), "Powinno być 2 gości o nazwisku Kowalski");
    }
    
    @Test
    @Order(13)
    @DisplayName("istniejeEmail sprawdza czy email istnieje")
    @Tag("dao")
    @Tag("wyszukiwanie")
    void testIstniejeEmail() {
        // Jeśli: gość jest zapisany
        goscieDAO.zapisz(gosc1);
        
        // Gdy: sprawdzany jest email
        boolean istnieje = goscieDAO.istniejeEmail("jan@test.pl");
        boolean nieIstnieje = goscieDAO.istniejeEmail("nieistnieje@test.pl");
        
        // Wtedy: prawidłowe wyniki
        assertTrue(istnieje, "Email jan@test.pl powinien istnieć");
        assertFalse(nieIstnieje, "Email nieistnieje@test.pl nie powinien istnieć");
    }
    
    // ========== TESTY PARAMETRYZOWANE ==========
    
    @ParameterizedTest(name = "Wyszukiwanie emaila: {0}")
    @Order(14)
    @DisplayName("Testy parametryzowane wyszukiwania case-insensitive - ValueSource")
    @Tag("parametryzowany")
    @Tag("wyszukiwanie")
    @ValueSource(strings = {"jan@test.pl", "JAN@TEST.PL", "Jan@Test.Pl"})
    void testZnajdzPoEmail_CaseInsensitive(String email) {
        // Jeśli: gość jest zapisany
        goscieDAO.zapisz(gosc1);
        
        // Gdy: wyszukiwany jest gość z różną wielkością liter
        Optional<Gosc> wynik = goscieDAO.znajdzPoEmail(email);
        
        // Wtedy: gość został znaleziony
        assertTrue(wynik.isPresent(), "Powinien znaleźć gościa dla: " + email);
    }
    
    @Test
    @Order(15)
    @DisplayName("wyczysc usuwa wszystkich gości")
    @Tag("dao")
    @Tag("czyszczenie")
    void testWyczysc() {
        // Jeśli: baza zawiera gości
        goscieDAO.zapisz(gosc1);
        goscieDAO.zapisz(gosc2);
        
        // Gdy: czyszczona jest baza
        goscieDAO.wyczysc();
        
        // Wtedy: baza jest pusta
        assertEquals(0, goscieDAO.liczba(), "Baza powinna być pusta");
    }
}
