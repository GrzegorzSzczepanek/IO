package hotel.testyfitnesse;

import fit.ColumnFixture;
import hotel.model.Rezerwacja;

import java.util.Optional;

/**
 * Klasa testująca przypadek użycia: Zamelduj Gościa.
 * Rozszerza ColumnFixture - atrybuty publiczne są danymi wejściowymi z tabeli FitNesse.
 * 
 * Testowany przypadek użycia: Zameldowanie gościa na podstawie rezerwacji
 * Kontroler: ZameldowanieKontroler.zameldujGoscia(int idRezerwacji)
 * 
 * @author Iuliia Kapustinskaia
 */
public class TestZameldujGoscia extends ColumnFixture {
    
    // ========== DANE WEJŚCIOWE (pobierane z tabeli FitNesse) ==========
    
    /** ID rezerwacji do zameldowania - wartość pobierana z tabeli testowej */
    public int idRezerwacji;
    
    // ========== OPERACJE TESTUJĄCE ==========
    
    /**
     * Testuje operację zameldowania gościa.
     * Wywołuje operację realizującą przypadek użycia i sprawdza wynik.
     * 
     * @return true jeśli zameldowanie się powiodło, false w przeciwnym razie
     */
    public boolean zameldujGoscia() {
        // 1. Zapisanie stanu przed operacją
        int zameldowaniPrzed = dajLiczbeZameldowanych();
        String statusPrzed = dajStatusRezerwacji();
        
        // 2. Wykonanie testowanej operacji (przypadek użycia)
        boolean wynik = SetUp.zameldowanieKontroler.zameldujGoscia(idRezerwacji);
        
        // 3. Zapisanie stanu po operacji
        int zameldowaniPo = dajLiczbeZameldowanych();
        String statusPo = dajStatusRezerwacji();
        
        // 4. Zwrócenie wyniku operacji
        return wynik;
    }
    
    // ========== OPERACJE SPRAWDZAJĄCE STAN WARSTWY ENCJI ==========
    
    /**
     * Zwraca całkowitą liczbę rezerwacji w systemie.
     * 
     * @return liczba wszystkich rezerwacji
     */
    public int dajLiczbeRezerwacji() {
        return SetUp.rezerwacjeDAO.liczba();
    }
    
    /**
     * Zwraca liczbę zameldowanych rezerwacji.
     * 
     * @return liczba rezerwacji o statusie ZAMELDOWANA
     */
    public int dajLiczbeZameldowanych() {
        return SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.ZAMELDOWANA).size();
    }
    
    /**
     * Zwraca liczbę rezerwacji oczekujących na zameldowanie (NOWA lub POTWIERDZONA).
     * 
     * @return liczba rezerwacji gotowych do zameldowania
     */
    public int dajLiczbeOczekujacychNaZameldowanie() {
        int nowe = SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.NOWA).size();
        int potwierdzone = SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.POTWIERDZONA).size();
        return nowe + potwierdzone;
    }
    
    /**
     * Zwraca status rezerwacji o podanym ID.
     * 
     * @return status rezerwacji jako String lub "BRAK" jeśli nie istnieje
     */
    public String dajStatusRezerwacji() {
        Optional<Rezerwacja> rezerwacja = SetUp.rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isPresent()) {
            return rezerwacja.get().getStatus().name();
        }
        return "BRAK";
    }
    
    /**
     * Sprawdza czy rezerwacja o podanym ID istnieje.
     * 
     * @return true jeśli rezerwacja istnieje
     */
    public boolean czyRezerwacjaIstnieje() {
        return SetUp.rezerwacjeDAO.pobierz(idRezerwacji).isPresent();
    }
    
    /**
     * Sprawdza czy pokój związany z rezerwacją jest oznaczony jako niedostępny.
     * 
     * @return true jeśli pokój jest zajęty (niedostępny)
     */
    public boolean czyPokojZajety() {
        Optional<Rezerwacja> rezerwacja = SetUp.rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isPresent()) {
            int numerPokoju = rezerwacja.get().getPokoj().getNumer();
            return !SetUp.pokojeDAO.czyDostepny(numerPokoju);
        }
        return false;
    }
    
    /**
     * Zwraca liczbę dostępnych pokoi.
     * 
     * @return liczba dostępnych pokoi
     */
    public int dajLiczbeDostepnychPokoi() {
        return SetUp.pokojeDAO.pobierzDostepne().size();
    }
}
