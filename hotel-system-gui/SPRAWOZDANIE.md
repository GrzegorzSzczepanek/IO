# SPRAWOZDANIE
# Laboratoria 14 - Testowanie akceptacyjne realizacji przypadków użycia

---

## Informacje podstawowe

**Autorzy:**
| Imię i nazwisko | Nr albumu |
|-----------------|-----------|
| Grzegorz Szczepanek | [nr albumu] |
| Iuliia Kapustinskaia | [nr albumu] |
| Lukerya Prakofyeva | [nr albumu] |

**Nazwa oprogramowania:** System Zarządzania Hotelem

**Opis dziedziny:** System umożliwia zarządzanie rezerwacjami hotelowymi, zameldowanie i wymeldowanie gości oraz zarządzanie profilami gości.

**Temat etapu:** Testowanie akceptacyjne realizacji przypadków użycia

---

## 1. Lista zmian w kodzie źródłowym

W celu wykonania testów akceptacyjnych z użyciem frameworku FitNesse, wprowadzono następujące zmiany:

### 1.1 Dodane pliki

| Plik | Opis |
|------|------|
| `src/main/java/hotel/testyfitnesse/SetUp.java` | Klasa Fixture przygotowująca testowane oprogramowanie |
| `src/main/java/hotel/testyfitnesse/TestAnulujRezerwacje.java` | Klasa testująca przypadek użycia "Anuluj Rezerwację" |
| `src/main/java/hotel/testyfitnesse/TestZameldujGoscia.java` | Klasa testująca przypadek użycia "Zamelduj Gościa" |
| `src/main/java/hotel/testyfitnesse/TestWymeldujGoscia.java` | Klasa testująca przypadek użycia "Wymelduj Gościa" |
| `start_fitnesse.sh` | Skrypt uruchamiający FitNesse z automatycznym wyborem portu |
| `FITNESSE_PAGES.txt` | Treści stron FitNesse do skopiowania |

### 1.2 Zmodyfikowane pliki

| Plik | Zmiana |
|------|--------|
| `pom.xml` | Dodano zależność do biblioteki FitNesse |

### 1.3 Istniejące metody wykorzystane do sprawdzania stanu

Projekt zawierał już metody umożliwiające sprawdzenie stanu warstwy encji:

- `RezerwacjeDAO.liczba()` - liczba rezerwacji
- `RezerwacjeDAO.pobierzAktywne()` - aktywne rezerwacje
- `RezerwacjeDAO.pobierzPoStatusie(Status)` - rezerwacje o danym statusie
- `GoscieDAO.liczba()` - liczba gości
- `PokojeDAO.liczba()` - liczba pokoi
- `PokojeDAO.czyDostepny(int)` - dostępność pokoju
- `PokojeDAO.pobierzDostepne()` - lista dostępnych pokoi

---

## 2. Kod klasy SetUp

**Plik:** `src/main/java/hotel/testyfitnesse/SetUp.java`

```java
     1	package hotel.testyfitnesse;
     2	
     3	import fit.Fixture;
     4	import hotel.controller.GoscieKontroler;
     5	import hotel.controller.RezerwacjeKontroler;
     6	import hotel.controller.WymeldowanieKontroler;
     7	import hotel.controller.ZameldowanieKontroler;
     8	import hotel.dao.GoscieDAO;
     9	import hotel.dao.PokojeDAO;
    10	import hotel.dao.RezerwacjeDAO;
    11	import hotel.model.FabrykaGosci;
    12	import hotel.model.Gosc;
    13	import hotel.model.HotelModel;
    14	import hotel.model.Pokoj;
    15	import hotel.model.Rezerwacja;
    16	
    17	import java.time.LocalDate;
    18	
    19	/**
    20	 * Klasa SetUp przygotowująca testowane oprogramowanie dla FitNesse.
    21	 * Tworzy i łączy wszystkie warstwy systemu hotelowego.
    22	 * 
    23	 * @author Grzegorz Szczepanek
    24	 * @author Iuliia Kapustinskaia
    25	 * @author Lukerya Prakofyeva
    26	 */
    27	public class SetUp extends Fixture {
    28	    
    29	    // ========== WARSTWA MODELU ==========
    30	    /** Główny model systemu hotelowego */
    31	    public static HotelModel model;
    32	    
    33	    // ========== WARSTWA KONTROLERÓW ==========
    34	    /** Kontroler zarządzający rezerwacjami */
    35	    public static RezerwacjeKontroler rezerwacjeKontroler;
    36	    
    37	    /** Kontroler zarządzający zameldowaniem */
    38	    public static ZameldowanieKontroler zameldowanieKontroler;
    39	    
    40	    /** Kontroler zarządzający wymeldowaniem */
    41	    public static WymeldowanieKontroler wymeldowanieKontroler;
    42	    
    43	    /** Kontroler zarządzający gośćmi */
    44	    public static GoscieKontroler goscieKontroler;
    45	    
    46	    // ========== WARSTWA DAO (dostęp do stanu encji) ==========
    47	    /** DAO rezerwacji - dostęp do stanu rezerwacji */
    48	    public static RezerwacjeDAO rezerwacjeDAO;
    49	    
    50	    /** DAO pokoi - dostęp do stanu pokoi */
    51	    public static PokojeDAO pokojeDAO;
    52	    
    53	    /** DAO gości - dostęp do stanu gości */
    54	    public static GoscieDAO goscieDAO;
    55	    
    56	    // ========== DANE TESTOWE ==========
    57	    /** Testowy gość używany w testach */
    58	    public static Gosc testGosc;
    59	    
    60	    /** Testowy pokój używany w testach */
    61	    public static Pokoj testPokoj;
    62	    
    63	    /** Testowa rezerwacja używana w testach */
    64	    public static Rezerwacja testRezerwacja;
    65	    
    66	    /**
    67	     * Konstruktor SetUp - tworzy całe testowane oprogramowanie.
    68	     * Inicjalizuje wszystkie warstwy i łączy je ze sobą.
    69	     */
    70	    public SetUp() {
    71	        // Resetowanie liczników ID przed każdym zestawem testów
    72	        Gosc.resetIdCounter();
    73	        Rezerwacja.resetIdCounter();
    74	        
    75	        // 1. Tworzenie warstwy DAO
    76	        rezerwacjeDAO = new RezerwacjeDAO();
    77	        pokojeDAO = new PokojeDAO();
    78	        goscieDAO = new GoscieDAO();
    79	        
    80	        // 2. Tworzenie warstwy modelu z wstrzykiwaniem DAO
    81	        model = new HotelModel(rezerwacjeDAO, pokojeDAO, goscieDAO, new FabrykaGosci());
    82	        
    83	        // 3. Tworzenie warstwy kontrolerów z wstrzykiwaniem modelu
    84	        rezerwacjeKontroler = new RezerwacjeKontroler(model);
    85	        zameldowanieKontroler = new ZameldowanieKontroler(model);
    86	        wymeldowanieKontroler = new WymeldowanieKontroler(model);
    87	        goscieKontroler = new GoscieKontroler(model);
    88	        
    89	        // 4. Przygotowanie danych testowych
    90	        przygotujDaneTestowe();
    91	    }
    92	    
    93	    /**
    94	     * Przygotowuje początkowe dane testowe w systemie.
    95	     * Tworzy przykładowego gościa, pokój i rezerwację.
    96	     */
    97	    private void przygotujDaneTestowe() {
    98	        // Tworzenie testowego gościa
    99	        testGosc = new Gosc("Jan", "Kowalski", "jan.kowalski@email.pl");
   100	        goscieDAO.zapisz(testGosc);
   101	        
   102	        // Tworzenie dodatkowych gości
   103	        Gosc gosc2 = new Gosc("Anna", "Nowak", "anna.nowak@email.pl");
   104	        goscieDAO.zapisz(gosc2);
   105	        
   106	        Gosc gosc3 = new Gosc("Piotr", "Wiśniewski", "piotr.wisniewski@email.pl");
   107	        goscieDAO.zapisz(gosc3);
   108	        
   109	        // Tworzenie testowych pokoi
   110	        testPokoj = new Pokoj(101, "Jednoosobowy", 150.0);
   111	        pokojeDAO.zapisz(testPokoj);
   112	        
   113	        Pokoj pokoj2 = new Pokoj(102, "Dwuosobowy", 250.0);
   114	        pokojeDAO.zapisz(pokoj2);
   115	        
   116	        Pokoj pokoj3 = new Pokoj(201, "Apartament", 500.0);
   117	        pokojeDAO.zapisz(pokoj3);
   118	        
   119	        // Tworzenie testowej rezerwacji (na przyszły tydzień) - ID=1, status NOWA
   120	        LocalDate dataOd = LocalDate.now().plusDays(7);
   121	        LocalDate dataDo = LocalDate.now().plusDays(10);
   122	        testRezerwacja = new Rezerwacja(dataOd, dataDo, testGosc, testPokoj);
   123	        rezerwacjeDAO.zapisz(testRezerwacja);
   124	        
   125	        // Rezerwacja ID=2 - status POTWIERDZONA (gotowa do zameldowania)
   126	        LocalDate dataOd2 = LocalDate.now();
   127	        LocalDate dataDo2 = LocalDate.now().plusDays(3);
   128	        Rezerwacja rezerwacja2 = new Rezerwacja(dataOd2, dataDo2, gosc2, pokoj2);
   129	        rezerwacja2.setStatus(Rezerwacja.Status.POTWIERDZONA);
   130	        rezerwacjeDAO.zapisz(rezerwacja2);
   131	        
   132	        // Rezerwacja ID=3 - status ZAMELDOWANA (gotowa do wymeldowania)
   133	        LocalDate dataOd3 = LocalDate.now().minusDays(2);
   134	        LocalDate dataDo3 = LocalDate.now().plusDays(1);
   135	        Rezerwacja rezerwacja3 = new Rezerwacja(dataOd3, dataDo3, gosc3, pokoj3);
   136	        rezerwacja3.setStatus(Rezerwacja.Status.ZAMELDOWANA);
   137	        rezerwacjeDAO.zapisz(rezerwacja3);
   138	    }
   139	    
   140	    /**
   141	     * Resetuje stan systemu do początkowego.
   142	     */
   143	    public static void resetujStan() {
   144	        rezerwacjeDAO.wyczysc();
   145	        pokojeDAO.wyczysc();
   146	        goscieDAO.wyczysc();
   147	        Gosc.resetIdCounter();
   148	        Rezerwacja.resetIdCounter();
   149	    }
   150	}
```

---

## 3. Kod klasy TestAnulujRezerwacje

**Plik:** `src/main/java/hotel/testyfitnesse/TestAnulujRezerwacje.java`

```java
     1	package hotel.testyfitnesse;
     2	
     3	import fit.ColumnFixture;
     4	import hotel.model.Rezerwacja;
     5	
     6	import java.util.Optional;
     7	
     8	/**
     9	 * Klasa testująca przypadek użycia: Anuluj Rezerwację.
    10	 * Rozszerza ColumnFixture - atrybuty publiczne są danymi wejściowymi z tabeli FitNesse.
    11	 * 
    12	 * Testowany przypadek użycia: Anulowanie rezerwacji hotelowej
    13	 * Kontroler: RezerwacjeKontroler.anulujRezerwacje(int idRezerwacji)
    14	 * 
    15	 * @author Grzegorz Szczepanek
    16	 */
    17	public class TestAnulujRezerwacje extends ColumnFixture {
    18	    
    19	    // ========== DANE WEJŚCIOWE (pobierane z tabeli FitNesse) ==========
    20	    
    21	    /** ID rezerwacji do anulowania - wartość pobierana z tabeli testowej */
    22	    public int idRezerwacji;
    23	    
    24	    // ========== OPERACJE TESTUJĄCE ==========
    25	    
    26	    /**
    27	     * Testuje operację anulowania rezerwacji.
    28	     * Wywołuje operację realizującą przypadek użycia i sprawdza wynik.
    29	     * 
    30	     * @return true jeśli anulowanie się powiodło, false w przeciwnym razie
    31	     */
    32	    public boolean anulujRezerwacje() {
    33	        // 1. Zapisanie stanu przed operacją
    34	        int liczbaPrzed = dajLiczbeRezerwacjiAktywnych();
    35	        String statusPrzed = dajStatusRezerwacji();
    36	        
    37	        // 2. Wykonanie testowanej operacji (przypadek użycia)
    38	        boolean wynik = SetUp.rezerwacjeKontroler.anulujRezerwacje(idRezerwacji);
    39	        
    40	        // 3. Zapisanie stanu po operacji
    41	        int liczbaPo = dajLiczbeRezerwacjiAktywnych();
    42	        String statusPo = dajStatusRezerwacji();
    43	        
    44	        // 4. Zwrócenie wyniku operacji
    45	        return wynik;
    46	    }
    47	    
    48	    // ========== OPERACJE SPRAWDZAJĄCE STAN WARSTWY ENCJI ==========
    49	    
    50	    /**
    51	     * Zwraca całkowitą liczbę rezerwacji w systemie.
    52	     */
    53	    public int dajLiczbeRezerwacji() {
    54	        return SetUp.rezerwacjeDAO.liczba();
    55	    }
    56	    
    57	    /**
    58	     * Zwraca liczbę aktywnych rezerwacji (nie anulowanych i nie wymeldowanych).
    59	     */
    60	    public int dajLiczbeRezerwacjiAktywnych() {
    61	        return SetUp.rezerwacjeDAO.pobierzAktywne().size();
    62	    }
    63	    
    64	    /**
    65	     * Zwraca liczbę anulowanych rezerwacji.
    66	     */
    67	    public int dajLiczbeRezerwacjiAnulowanych() {
    68	        return SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.ANULOWANA).size();
    69	    }
    70	    
    71	    /**
    72	     * Zwraca status rezerwacji o podanym ID.
    73	     */
    74	    public String dajStatusRezerwacji() {
    75	        Optional<Rezerwacja> rezerwacja = SetUp.rezerwacjeDAO.pobierz(idRezerwacji);
    76	        if (rezerwacja.isPresent()) {
    77	            return rezerwacja.get().getStatus().name();
    78	        }
    79	        return "BRAK";
    80	    }
    81	    
    82	    /**
    83	     * Sprawdza czy rezerwacja o podanym ID istnieje.
    84	     */
    85	    public boolean czyRezerwacjaIstnieje() {
    86	        return SetUp.rezerwacjeDAO.pobierz(idRezerwacji).isPresent();
    87	    }
    88	    
    89	    /**
    90	     * Zwraca opłatę za anulowanie rezerwacji.
    91	     */
    92	    public double dajOplateZaAnulowanie() {
    93	        return SetUp.rezerwacjeKontroler.pobierzOplateZaAnulowanie(idRezerwacji);
    94	    }
    95	}
```

---

## 4. Kod klasy TestZameldujGoscia

**Plik:** `src/main/java/hotel/testyfitnesse/TestZameldujGoscia.java`

```java
     1	package hotel.testyfitnesse;
     2	
     3	import fit.ColumnFixture;
     4	import hotel.model.Rezerwacja;
     5	
     6	import java.util.Optional;
     7	
     8	/**
     9	 * Klasa testująca przypadek użycia: Zamelduj Gościa.
    10	 * Rozszerza ColumnFixture - atrybuty publiczne są danymi wejściowymi z tabeli FitNesse.
    11	 * 
    12	 * Testowany przypadek użycia: Zameldowanie gościa na podstawie rezerwacji
    13	 * Kontroler: ZameldowanieKontroler.zameldujGoscia(int idRezerwacji)
    14	 * 
    15	 * @author Iuliia Kapustinskaia
    16	 */
    17	public class TestZameldujGoscia extends ColumnFixture {
    18	    
    19	    // ========== DANE WEJŚCIOWE (pobierane z tabeli FitNesse) ==========
    20	    
    21	    /** ID rezerwacji do zameldowania */
    22	    public int idRezerwacji;
    23	    
    24	    // ========== OPERACJE TESTUJĄCE ==========
    25	    
    26	    /**
    27	     * Testuje operację zameldowania gościa.
    28	     */
    29	    public boolean zameldujGoscia() {
    30	        // 1. Zapisanie stanu przed operacją
    31	        int zameldowaniPrzed = dajLiczbeZameldowanych();
    32	        String statusPrzed = dajStatusRezerwacji();
    33	        
    34	        // 2. Wykonanie testowanej operacji (przypadek użycia)
    35	        boolean wynik = SetUp.zameldowanieKontroler.zameldujGoscia(idRezerwacji);
    36	        
    37	        // 3. Zapisanie stanu po operacji
    38	        int zameldowaniPo = dajLiczbeZameldowanych();
    39	        String statusPo = dajStatusRezerwacji();
    40	        
    41	        // 4. Zwrócenie wyniku operacji
    42	        return wynik;
    43	    }
    44	    
    45	    // ========== OPERACJE SPRAWDZAJĄCE STAN WARSTWY ENCJI ==========
    46	    
    47	    public int dajLiczbeRezerwacji() {
    48	        return SetUp.rezerwacjeDAO.liczba();
    49	    }
    50	    
    51	    public int dajLiczbeZameldowanych() {
    52	        return SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.ZAMELDOWANA).size();
    53	    }
    54	    
    55	    public String dajStatusRezerwacji() {
    56	        Optional<Rezerwacja> rezerwacja = SetUp.rezerwacjeDAO.pobierz(idRezerwacji);
    57	        if (rezerwacja.isPresent()) {
    58	            return rezerwacja.get().getStatus().name();
    59	        }
    60	        return "BRAK";
    61	    }
    62	    
    63	    public boolean czyRezerwacjaIstnieje() {
    64	        return SetUp.rezerwacjeDAO.pobierz(idRezerwacji).isPresent();
    65	    }
    66	    
    67	    public boolean czyPokojZajety() {
    68	        Optional<Rezerwacja> rezerwacja = SetUp.rezerwacjeDAO.pobierz(idRezerwacji);
    69	        if (rezerwacja.isPresent()) {
    70	            int numerPokoju = rezerwacja.get().getPokoj().getNumer();
    71	            return !SetUp.pokojeDAO.czyDostepny(numerPokoju);
    72	        }
    73	        return false;
    74	    }
    75	    
    76	    public int dajLiczbeDostepnychPokoi() {
    77	        return SetUp.pokojeDAO.pobierzDostepne().size();
    78	    }
    79	}
```

---

## 5. Kod klasy TestWymeldujGoscia

**Plik:** `src/main/java/hotel/testyfitnesse/TestWymeldujGoscia.java`

```java
     1	package hotel.testyfitnesse;
     2	
     3	import fit.ColumnFixture;
     4	import hotel.model.Rezerwacja;
     5	
     6	import java.util.Optional;
     7	
     8	/**
     9	 * Klasa testująca przypadek użycia: Wymelduj Gościa.
    10	 * Rozszerza ColumnFixture - atrybuty publiczne są danymi wejściowymi z tabeli FitNesse.
    11	 * 
    12	 * Testowany przypadek użycia: Wymeldowanie gościa z hotelu
    13	 * Kontroler: WymeldowanieKontroler.wymeldujGoscia(int idRezerwacji)
    14	 * 
    15	 * @author Lukerya Prakofyeva
    16	 */
    17	public class TestWymeldujGoscia extends ColumnFixture {
    18	    
    19	    // ========== DANE WEJŚCIOWE (pobierane z tabeli FitNesse) ==========
    20	    
    21	    /** ID rezerwacji do wymeldowania */
    22	    public int idRezerwacji;
    23	    
    24	    /** Liczba godzin opóźnienia przy wymeldowaniu */
    25	    public int godzinyOpoznienia;
    26	    
    27	    // ========== OPERACJE TESTUJĄCE ==========
    28	    
    29	    /**
    30	     * Testuje operację wymeldowania gościa.
    31	     */
    32	    public boolean wymeldujGoscia() {
    33	        // 1. Zapisanie stanu przed operacją
    34	        int wymeldowaniPrzed = dajLiczbeWymeldowanych();
    35	        int zameldowaniPrzed = dajLiczbeZameldowanych();
    36	        String statusPrzed = dajStatusRezerwacji();
    37	        
    38	        // 2. Wykonanie testowanej operacji (przypadek użycia)
    39	        boolean wynik = SetUp.wymeldowanieKontroler.wymeldujGoscia(idRezerwacji);
    40	        
    41	        // 3. Zapisanie stanu po operacji
    42	        int wymeldowaniPo = dajLiczbeWymeldowanych();
    43	        int zameldowaniPo = dajLiczbeZameldowanych();
    44	        String statusPo = dajStatusRezerwacji();
    45	        
    46	        // 4. Zwrócenie wyniku operacji
    47	        return wynik;
    48	    }
    49	    
    50	    public double dajOplateZaPozneWymeldowanie() {
    51	        return SetUp.wymeldowanieKontroler.naliczOplateZaPozneWymeldowanie(
    52	                idRezerwacji, godzinyOpoznienia);
    53	    }
    54	    
    55	    // ========== OPERACJE SPRAWDZAJĄCE STAN WARSTWY ENCJI ==========
    56	    
    57	    public int dajLiczbeRezerwacji() {
    58	        return SetUp.rezerwacjeDAO.liczba();
    59	    }
    60	    
    61	    public int dajLiczbeWymeldowanych() {
    62	        return SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.WYMELDOWANA).size();
    63	    }
    64	    
    65	    public int dajLiczbeZameldowanych() {
    66	        return SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.ZAMELDOWANA).size();
    67	    }
    68	    
    69	    public String dajStatusRezerwacji() {
    70	        Optional<Rezerwacja> rezerwacja = SetUp.rezerwacjeDAO.pobierz(idRezerwacji);
    71	        if (rezerwacja.isPresent()) {
    72	            return rezerwacja.get().getStatus().name();
    73	        }
    74	        return "BRAK";
    75	    }
    76	    
    77	    public boolean czyRezerwacjaIstnieje() {
    78	        return SetUp.rezerwacjeDAO.pobierz(idRezerwacji).isPresent();
    79	    }
    80	    
    81	    public boolean czyPokojDostepny() {
    82	        Optional<Rezerwacja> rezerwacja = SetUp.rezerwacjeDAO.pobierz(idRezerwacji);
    83	        if (rezerwacja.isPresent()) {
    84	            int numerPokoju = rezerwacja.get().getPokoj().getNumer();
    85	            return SetUp.pokojeDAO.czyDostepny(numerPokoju);
    86	        }
    87	        return false;
    88	    }
    89	    
    90	    public int dajLiczbeDostepnychPokoi() {
    91	        return SetUp.pokojeDAO.pobierzDostepne().size();
    92	    }
    93	}
```

---

## 6. Treść strony SetUp

```
|import|
|hotel.testyfitnesse|

!|hotel.testyfitnesse.SetUp|
```

---

## 7. Treść strony testującej: AnulujRezerwacje

**Nazwa strony:** AnulujRezerwacje

```
!3 Test przypadku użycia: Anuluj Rezerwację

'''Opis:''' Test sprawdza poprawność działania operacji anulowania rezerwacji hotelowej.

'''Kontroler:''' RezerwacjeKontroler.anulujRezerwacje(int idRezerwacji)

'''Autor:''' Grzegorz Szczepanek

----

!|hotel.testyfitnesse.TestAnulujRezerwacje|
|idRezerwacji|anulujRezerwacje?|dajStatusRezerwacji?|dajLiczbeRezerwacjiAnulowanych?|
|1           |true             |ANULOWANA           |1                              |
|2           |false            |POTWIERDZONA        |1                              |
|3           |false            |ZAMELDOWANA         |1                              |
|999         |false            |BRAK                |1                              |
|0           |false            |BRAK                |1                              |
|-1          |false            |BRAK                |1                              |

----

'''Opis scenariuszy testowych:'''
 * Wiersz 1: Anulowanie rezerwacji o statusie NOWA - powinno się powieść
 * Wiersz 2: Anulowanie rezerwacji POTWIERDZONEJ - nie powinno się powieść
 * Wiersz 3: Anulowanie rezerwacji ZAMELDOWANEJ - nie powinno się powieść
 * Wiersz 4: Anulowanie nieistniejącej rezerwacji (ID=999) - false
 * Wiersz 5: Anulowanie z nieprawidłowym ID (0) - false
 * Wiersz 6: Anulowanie z ujemnym ID (-1) - false
```

---

## 8. Treść strony testującej: ZameldujGoscia

**Nazwa strony:** ZameldujGoscia

```
!3 Test przypadku użycia: Zamelduj Gościa

'''Opis:''' Test sprawdza poprawność działania operacji zameldowania gościa.

'''Kontroler:''' ZameldowanieKontroler.zameldujGoscia(int idRezerwacji)

'''Autor:''' Iuliia Kapustinskaia

----

!|hotel.testyfitnesse.TestZameldujGoscia|
|idRezerwacji|zameldujGoscia?|dajStatusRezerwacji?|dajLiczbeZameldowanych?|
|2           |true           |ZAMELDOWANA         |2                      |
|1           |false          |NOWA                |2                      |
|3           |false          |ZAMELDOWANA         |2                      |
|999         |false          |BRAK                |2                      |
|0           |false          |BRAK                |2                      |

----

'''Opis scenariuszy testowych:'''
 * Wiersz 1: Zameldowanie rezerwacji POTWIERDZONEJ (ID=2) - powinno się powieść
 * Wiersz 2: Zameldowanie rezerwacji NOWEJ (ID=1) - nie powinno się powieść
 * Wiersz 3: Ponowne zameldowanie już ZAMELDOWANEJ rezerwacji - nie powinno się powieść
 * Wiersz 4: Zameldowanie nieistniejącej rezerwacji - false
 * Wiersz 5: Zameldowanie z nieprawidłowym ID - false
```

---

## 9. Treść strony testującej: WymeldujGoscia

**Nazwa strony:** WymeldujGoscia

```
!3 Test przypadku użycia: Wymelduj Gościa

'''Opis:''' Test sprawdza poprawność działania operacji wymeldowania gościa.

'''Kontroler:''' WymeldowanieKontroler.wymeldujGoscia(int idRezerwacji)

'''Autor:''' Lukerya Prakofyeva

----

!|hotel.testyfitnesse.TestWymeldujGoscia|
|idRezerwacji|wymeldujGoscia?|dajStatusRezerwacji?|dajLiczbeWymeldowanych?|czyPokojDostepny?|
|3           |true           |WYMELDOWANA         |1                      |true             |
|1           |false          |NOWA                |1                      |false            |
|2           |false          |POTWIERDZONA        |1                      |false            |
|999         |false          |BRAK                |1                      |false            |
|0           |false          |BRAK                |1                      |false            |

----

!3 Test opłaty za późne wymeldowanie

!|hotel.testyfitnesse.TestWymeldujGoscia|
|idRezerwacji|godzinyOpoznienia|dajOplateZaPozneWymeldowanie?|
|3           |0                |0.0                          |
|3           |1                |25.0                         |
|3           |2                |50.0                         |
|3           |3                |75.0                         |
|999         |2                |0.0                          |

----

'''Opis scenariuszy testowych:'''
 * Wiersz 1: Wymeldowanie rezerwacji ZAMELDOWANEJ - powinno się powieść
 * Wiersz 2: Wymeldowanie rezerwacji NOWEJ - nie powinno się powieść
 * Wiersz 3: Wymeldowanie rezerwacji POTWIERDZONEJ - nie powinno się powieść
 * Wiersz 4-5: Wymeldowanie nieistniejącej/nieprawidłowej rezerwacji - false
```

---

## 10. Wynik wykonania zestawu testów

[MIEJSCE NA SCREENSHOT Z PRZEGLĄDARKI]

*Należy wkleić zdjęcie ekranu przeglądarki pokazujące wyniki testów z kolorowymi komórkami tabeli (zielone = test przeszedł, czerwone = test nie przeszedł).*

---

## Instrukcja uruchomienia testów

1. **Kompilacja projektu:**
   ```bash
   cd hotel-system
   mvn compile
   ```

2. **Uruchomienie FitNesse:**
   ```bash
   ./start_fitnesse.sh
   ```
   lub ręcznie:
   ```bash
   java -jar lib/fitnesse-standalone.jar -p 8080
   ```

3. **Otwarcie przeglądarki:**
   ```
   http://localhost:8080
   ```

4. **Utworzenie stron testowych** (według instrukcji w pliku FITNESSE_PAGES.txt)

5. **Uruchomienie testów:**
   - Pojedynczy test: wejdź na stronę testu → kliknij **Test**
   - Cały zestaw: wejdź na **HotelSystem** → kliknij **Suite**
