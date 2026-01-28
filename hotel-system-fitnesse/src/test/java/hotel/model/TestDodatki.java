package hotel.model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasa testów jednostkowych dla klas Sniadanie i Parking (implementacje IDodatek).
 * Testy operacji niezależnych - warstwa encji, wzorzec Dekorator.
 * 
 * @author Grzegorz - System Zarządzania Hotelem
 */
@DisplayName("Testy klas Sniadanie i Parking (IDodatek)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestDodatki {
    
    private Sniadanie sniadanie;
    private Parking parking;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Rozpoczęcie testów klas dodatków");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("Zakończenie testów klas dodatków");
    }
    
    @BeforeEach
    void setUp() {
        // Jeśli: przygotowanie danych przed testem
        sniadanie = new Sniadanie(50.0, 3);
        parking = new Parking(30.0, 5);
    }
    
    @AfterEach
    void tearDown() {
        sniadanie = null;
        parking = null;
    }
    
    // ========== TESTY SNIADANIE ==========
    
    @Test
    @Order(1)
    @DisplayName("Sniadanie: konstruktor tworzy poprawny obiekt")
    @Tag("konstruktor")
    @Tag("encja")
    @Tag("sniadanie")
    void testSniadanie_Konstruktor() {
        // Jeśli: podano cenę i liczbę dni
        Sniadanie s = new Sniadanie(60.0, 4);
        
        // Gdy/Wtedy: obiekt ma prawidłowe wartości
        assertAll(
            () -> assertEquals(60.0, s.getCena(), 0.01),
            () -> assertEquals(4, s.getLiczbaDni())
        );
    }
    
    @Test
    @Order(2)
    @DisplayName("Sniadanie: konstruktor domyślny ustawia cenę 50 zł")
    @Tag("konstruktor")
    @Tag("encja")
    @Tag("sniadanie")
    void testSniadanie_KonstruktorDomyslny() {
        // Jeśli: użyto konstruktora z samą liczbą dni
        Sniadanie s = new Sniadanie(5);
        
        // Gdy/Wtedy: cena domyślna to 50 zł
        assertEquals(50.0, s.getCena(), 0.01, "Domyślna cena powinna być 50 zł");
    }
    
    @Test
    @Order(3)
    @DisplayName("Sniadanie: obliczDodatkowyKoszt zwraca prawidłową wartość")
    @Tag("obliczenia")
    @Tag("encja")
    @Tag("sniadanie")
    void testSniadanie_ObliczDodatkowyKoszt() {
        // Jeśli: śniadanie 50 zł x 3 dni
        // Gdy: obliczany jest koszt
        double koszt = sniadanie.obliczDodatkowyKoszt();
        
        // Wtedy: koszt = 150 zł
        assertEquals(150.0, koszt, 0.01, "Koszt powinien być 150 zł");
    }
    
    @Test
    @Order(4)
    @DisplayName("Sniadanie: getOpis zwraca czytelny opis")
    @Tag("opis")
    @Tag("encja")
    @Tag("sniadanie")
    void testSniadanie_GetOpis() {
        // Jeśli: śniadanie istnieje
        // Gdy: pobierany jest opis
        String opis = sniadanie.getOpis();
        
        // Wtedy: opis zawiera słowo "Śniadanie"
        assertNotNull(opis);
        assertTrue(opis.contains("Śniadanie"), "Opis powinien zawierać słowo 'Śniadanie'");
        assertTrue(opis.contains("3"), "Opis powinien zawierać liczbę dni");
    }
    
    @Test
    @Order(5)
    @DisplayName("Sniadanie: konstruktor rzuca wyjątek dla ujemnej ceny")
    @Tag("konstruktor")
    @Tag("walidacja")
    @Tag("sniadanie")
    void testSniadanie_UjemnaCena() {
        // Jeśli: cena jest ujemna
        // Gdy/Wtedy: konstruktor rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Sniadanie(-10.0, 3),
            "Powinien zostać rzucony wyjątek dla ujemnej ceny"
        );
    }
    
    @Test
    @Order(6)
    @DisplayName("Sniadanie: konstruktor rzuca wyjątek dla 0 dni")
    @Tag("konstruktor")
    @Tag("walidacja")
    @Tag("sniadanie")
    void testSniadanie_ZeroDni() {
        // Jeśli: liczba dni to 0
        // Gdy/Wtedy: konstruktor rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Sniadanie(50.0, 0),
            "Powinien zostać rzucony wyjątek dla 0 dni"
        );
    }
    
    // ========== TESTY PARKING ==========
    
    @Test
    @Order(7)
    @DisplayName("Parking: konstruktor tworzy poprawny obiekt")
    @Tag("konstruktor")
    @Tag("encja")
    @Tag("parking")
    void testParking_Konstruktor() {
        // Jeśli: podano cenę i liczbę dni
        Parking p = new Parking(40.0, 7);
        
        // Gdy/Wtedy: obiekt ma prawidłowe wartości
        assertAll(
            () -> assertEquals(40.0, p.getCena(), 0.01),
            () -> assertEquals(7, p.getLiczbaDni())
        );
    }
    
    @Test
    @Order(8)
    @DisplayName("Parking: konstruktor domyślny ustawia cenę 30 zł")
    @Tag("konstruktor")
    @Tag("encja")
    @Tag("parking")
    void testParking_KonstruktorDomyslny() {
        // Jeśli: użyto konstruktora z samą liczbą dni
        Parking p = new Parking(10);
        
        // Gdy/Wtedy: cena domyślna to 30 zł
        assertEquals(30.0, p.getCena(), 0.01, "Domyślna cena powinna być 30 zł");
    }
    
    @Test
    @Order(9)
    @DisplayName("Parking: obliczDodatkowyKoszt zwraca prawidłową wartość")
    @Tag("obliczenia")
    @Tag("encja")
    @Tag("parking")
    void testParking_ObliczDodatkowyKoszt() {
        // Jeśli: parking 30 zł x 5 dni
        // Gdy: obliczany jest koszt
        double koszt = parking.obliczDodatkowyKoszt();
        
        // Wtedy: koszt = 150 zł
        assertEquals(150.0, koszt, 0.01, "Koszt powinien być 150 zł");
    }
    
    @Test
    @Order(10)
    @DisplayName("Parking: getOpis zwraca czytelny opis")
    @Tag("opis")
    @Tag("encja")
    @Tag("parking")
    void testParking_GetOpis() {
        // Jeśli: parking istnieje
        // Gdy: pobierany jest opis
        String opis = parking.getOpis();
        
        // Wtedy: opis zawiera słowo "Parking"
        assertNotNull(opis);
        assertTrue(opis.contains("Parking"), "Opis powinien zawierać słowo 'Parking'");
    }
    
    @Test
    @Order(11)
    @DisplayName("Parking: konstruktor rzuca wyjątek dla ujemnej ceny")
    @Tag("konstruktor")
    @Tag("walidacja")
    @Tag("parking")
    void testParking_UjemnaCena() {
        // Jeśli: cena jest ujemna
        // Gdy/Wtedy: konstruktor rzuca wyjątek
        assertThrows(
            IllegalArgumentException.class,
            () -> new Parking(-20.0, 5),
            "Powinien zostać rzucony wyjątek dla ujemnej ceny"
        );
    }
    
    // ========== TESTY PARAMETRYZOWANE ==========
    
    @ParameterizedTest(name = "Test śniadania: {0} zł x {1} dni = {2} zł")
    @Order(12)
    @DisplayName("Testy parametryzowane kosztów śniadania - CsvSource")
    @Tag("parametryzowany")
    @Tag("obliczenia")
    @Tag("sniadanie")
    @CsvSource({
        "50.0, 1, 50.0",
        "50.0, 3, 150.0",
        "60.0, 5, 300.0",
        "45.0, 7, 315.0",
        "100.0, 2, 200.0"
    })
    void testSniadanie_ObliczKoszt_Parametryzowany(double cena, int dni, double oczekiwanyKoszt) {
        // Jeśli: podano cenę i dni
        Sniadanie s = new Sniadanie(cena, dni);
        
        // Gdy: obliczany jest koszt
        double koszt = s.obliczDodatkowyKoszt();
        
        // Wtedy: koszt jest prawidłowy
        assertEquals(oczekiwanyKoszt, koszt, 0.01);
    }
    
    @ParameterizedTest(name = "Test parkingu: {0} zł x {1} dni = {2} zł")
    @Order(13)
    @DisplayName("Testy parametryzowane kosztów parkingu - CsvSource")
    @Tag("parametryzowany")
    @Tag("obliczenia")
    @Tag("parking")
    @CsvSource({
        "30.0, 1, 30.0",
        "30.0, 5, 150.0",
        "25.0, 7, 175.0",
        "40.0, 3, 120.0",
        "35.0, 10, 350.0"
    })
    void testParking_ObliczKoszt_Parametryzowany(double cena, int dni, double oczekiwanyKoszt) {
        // Jeśli: podano cenę i dni
        Parking p = new Parking(cena, dni);
        
        // Gdy: obliczany jest koszt
        double koszt = p.obliczDodatkowyKoszt();
        
        // Wtedy: koszt jest prawidłowy
        assertEquals(oczekiwanyKoszt, koszt, 0.01);
    }
    
    @ParameterizedTest(name = "Test nieprawidłowej liczby dni: {0}")
    @Order(14)
    @DisplayName("Testy parametryzowane nieprawidłowych dni - ValueSource")
    @Tag("parametryzowany")
    @Tag("walidacja")
    @ValueSource(ints = {0, -1, -5, -100})
    void testNieprawidloweDni_ValueSource(int dni) {
        // Jeśli: liczba dni jest nieprawidłowa
        // Gdy/Wtedy: konstruktory rzucają wyjątki
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> new Sniadanie(dni)),
            () -> assertThrows(IllegalArgumentException.class, () -> new Parking(dni))
        );
    }
    
    // ========== TESTY INTERFEJSU IDODATEK ==========
    
    @Test
    @Order(15)
    @DisplayName("IDodatek: obie klasy implementują interfejs")
    @Tag("interfejs")
    @Tag("encja")
    void testIDodatek_Implementacja() {
        // Jeśli: obiekty są typu IDodatek
        IDodatek dodSniadanie = sniadanie;
        IDodatek dodParking = parking;
        
        // Gdy/Wtedy: można wywołać metody interfejsu
        assertAll(
            () -> assertTrue(dodSniadanie.obliczDodatkowyKoszt() > 0),
            () -> assertTrue(dodParking.obliczDodatkowyKoszt() > 0),
            () -> assertNotNull(dodSniadanie.getOpis()),
            () -> assertNotNull(dodParking.getOpis())
        );
    }
    
    @Test
    @Order(16)
    @DisplayName("Suma kosztów różnych dodatków")
    @Tag("obliczenia")
    @Tag("encja")
    void testSumaKosztow() {
        // Jeśli: mamy kilka dodatków
        IDodatek d1 = new Sniadanie(50.0, 3);  // 150 zł
        IDodatek d2 = new Parking(30.0, 3);    // 90 zł
        IDodatek d3 = new Sniadanie(60.0, 2);  // 120 zł
        
        // Gdy: sumowane są koszty
        double suma = d1.obliczDodatkowyKoszt() + d2.obliczDodatkowyKoszt() + d3.obliczDodatkowyKoszt();
        
        // Wtedy: suma = 360 zł
        assertEquals(360.0, suma, 0.01, "Suma kosztów powinna być 360 zł");
    }
}
