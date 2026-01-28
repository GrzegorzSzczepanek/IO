package hotel.testyfitnesse;

import fit.Fixture;
import hotel.controller.GoscieKontroler;
import hotel.controller.RezerwacjeKontroler;
import hotel.controller.WymeldowanieKontroler;
import hotel.controller.ZameldowanieKontroler;
import hotel.dao.GoscieDAO;
import hotel.dao.PokojeDAO;
import hotel.dao.RezerwacjeDAO;
import hotel.model.FabrykaGosci;
import hotel.model.Gosc;
import hotel.model.HotelModel;
import hotel.model.Pokoj;
import hotel.model.Rezerwacja;

import java.time.LocalDate;

/**
 * Klasa SetUp przygotowująca testowane oprogramowanie dla FitNesse.
 * Tworzy i łączy wszystkie warstwy systemu hotelowego.
 * 
 * @author Grzegorz Szczepanek
 * @author Iuliia Kapustinskaia
 * @author Lukerya Prakofyeva
 */
public class SetUp extends Fixture {
    
    // ========== WARSTWA MODELU ==========
    /** Główny model systemu hotelowego */
    public static HotelModel model;
    
    // ========== WARSTWA KONTROLERÓW ==========
    /** Kontroler zarządzający rezerwacjami */
    public static RezerwacjeKontroler rezerwacjeKontroler;
    
    /** Kontroler zarządzający zameldowaniem */
    public static ZameldowanieKontroler zameldowanieKontroler;
    
    /** Kontroler zarządzający wymeldowaniem */
    public static WymeldowanieKontroler wymeldowanieKontroler;
    
    /** Kontroler zarządzający gośćmi */
    public static GoscieKontroler goscieKontroler;
    
    // ========== WARSTWA DAO (dostęp do stanu encji) ==========
    /** DAO rezerwacji - dostęp do stanu rezerwacji */
    public static RezerwacjeDAO rezerwacjeDAO;
    
    /** DAO pokoi - dostęp do stanu pokoi */
    public static PokojeDAO pokojeDAO;
    
    /** DAO gości - dostęp do stanu gości */
    public static GoscieDAO goscieDAO;
    
    // ========== DANE TESTOWE ==========
    /** Testowy gość używany w testach */
    public static Gosc testGosc;
    
    /** Testowy pokój używany w testach */
    public static Pokoj testPokoj;
    
    /** Testowa rezerwacja używana w testach */
    public static Rezerwacja testRezerwacja;
    
    /**
     * Konstruktor SetUp - tworzy całe testowane oprogramowanie.
     * Inicjalizuje wszystkie warstwy i łączy je ze sobą.
     */
    public SetUp() {
        // Resetowanie liczników ID przed każdym zestawem testów
        Gosc.resetIdCounter();
        Rezerwacja.resetIdCounter();
        
        // 1. Tworzenie warstwy DAO
        rezerwacjeDAO = new RezerwacjeDAO();
        pokojeDAO = new PokojeDAO();
        goscieDAO = new GoscieDAO();
        
        // 2. Tworzenie warstwy modelu z wstrzykiwaniem DAO
        model = new HotelModel(rezerwacjeDAO, pokojeDAO, goscieDAO, new FabrykaGosci());
        
        // 3. Tworzenie warstwy kontrolerów z wstrzykiwaniem modelu
        rezerwacjeKontroler = new RezerwacjeKontroler(model);
        zameldowanieKontroler = new ZameldowanieKontroler(model);
        wymeldowanieKontroler = new WymeldowanieKontroler(model);
        goscieKontroler = new GoscieKontroler(model);
        
        // 4. Przygotowanie danych testowych
        przygotujDaneTestowe();
    }
    
    /**
     * Przygotowuje początkowe dane testowe w systemie.
     * Tworzy przykładowego gościa, pokój i rezerwację.
     */
    private void przygotujDaneTestowe() {
        // Tworzenie testowego gościa
        testGosc = new Gosc("Jan", "Kowalski", "jan.kowalski@email.pl");
        goscieDAO.zapisz(testGosc);
        
        // Tworzenie dodatkowych gości
        Gosc gosc2 = new Gosc("Anna", "Nowak", "anna.nowak@email.pl");
        goscieDAO.zapisz(gosc2);
        
        Gosc gosc3 = new Gosc("Piotr", "Wiśniewski", "piotr.wisniewski@email.pl");
        goscieDAO.zapisz(gosc3);
        
        // Tworzenie testowych pokoi
        testPokoj = new Pokoj(101, "Jednoosobowy", 150.0);
        pokojeDAO.zapisz(testPokoj);
        
        Pokoj pokoj2 = new Pokoj(102, "Dwuosobowy", 250.0);
        pokojeDAO.zapisz(pokoj2);
        
        Pokoj pokoj3 = new Pokoj(201, "Apartament", 500.0);
        pokojeDAO.zapisz(pokoj3);
        
        // Tworzenie testowej rezerwacji (na przyszły tydzień)
        LocalDate dataOd = LocalDate.now().plusDays(7);
        LocalDate dataDo = LocalDate.now().plusDays(10);
        testRezerwacja = new Rezerwacja(dataOd, dataDo, testGosc, testPokoj);
        rezerwacjeDAO.zapisz(testRezerwacja);
        
        // Tworzenie drugiej rezerwacji (potwierdzona, gotowa do zameldowania)
        LocalDate dataOd2 = LocalDate.now();
        LocalDate dataDo2 = LocalDate.now().plusDays(3);
        Rezerwacja rezerwacja2 = new Rezerwacja(dataOd2, dataDo2, gosc2, pokoj2);
        rezerwacja2.setStatus(Rezerwacja.Status.POTWIERDZONA);
        rezerwacjeDAO.zapisz(rezerwacja2);
        
        // Tworzenie trzeciej rezerwacji (zameldowana, gotowa do wymeldowania)
        LocalDate dataOd3 = LocalDate.now().minusDays(2);
        LocalDate dataDo3 = LocalDate.now().plusDays(1);
        Rezerwacja rezerwacja3 = new Rezerwacja(dataOd3, dataDo3, gosc3, pokoj3);
        rezerwacja3.setStatus(Rezerwacja.Status.ZAMELDOWANA);
        rezerwacjeDAO.zapisz(rezerwacja3);
    }
    
    /**
     * Resetuje stan systemu do początkowego (czyści wszystkie dane).
     * Używane między testami.
     */
    public static void resetujStan() {
        rezerwacjeDAO.wyczysc();
        pokojeDAO.wyczysc();
        goscieDAO.wyczysc();
        Gosc.resetIdCounter();
        Rezerwacja.resetIdCounter();
    }
}
