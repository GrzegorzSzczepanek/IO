package hotel.testyfitnesse;

import fit.ColumnFixture;
import hotel.model.Rezerwacja;

import java.util.Optional;

/**
 * Klasa testująca przypadek użycia: Wymelduj Gościa.
 * Rozszerza ColumnFixture - atrybuty publiczne są danymi wejściowymi z tabeli FitNesse.
 * 
 * Testowany przypadek użycia: Wymeldowanie gościa z hotelu
 * Kontroler: WymeldowanieKontroler.wymeldujGoscia(int idRezerwacji)
 * 
 * @author Lukerya Prakofyeva
 */
public class TestWymeldujGoscia extends ColumnFixture {
    
    // ========== DANE WEJŚCIOWE (pobierane z tabeli FitNesse) ==========
    
    /** ID rezerwacji do wymeldowania - wartość pobierana z tabeli testowej */
    public int idRezerwacji;
    
    /** Liczba godzin opóźnienia przy wymeldowaniu (opcjonalnie) */
    public int godzinyOpoznienia;
    
    // ========== OPERACJE TESTUJĄCE ==========
    
    /**
     * Testuje operację wymeldowania gościa.
     * Wywołuje operację realizującą przypadek użycia i sprawdza wynik.
     * 
     * @return true jeśli wymeldowanie się powiodło, false w przeciwnym razie
     */
    public boolean wymeldujGoscia() {
        // 1. Zapisanie stanu przed operacją
        int wymeldowaniPrzed = dajLiczbeWymeldowanych();
        int zameldowaniPrzed = dajLiczbeZameldowanych();
        String statusPrzed = dajStatusRezerwacji();
        
        // 2. Wykonanie testowanej operacji (przypadek użycia)
        boolean wynik = SetUp.wymeldowanieKontroler.wymeldujGoscia(idRezerwacji);
        
        // 3. Zapisanie stanu po operacji
        int wymeldowaniPo = dajLiczbeWymeldowanych();
        int zameldowaniPo = dajLiczbeZameldowanych();
        String statusPo = dajStatusRezerwacji();
        
        // 4. Zwrócenie wyniku operacji
        return wynik;
    }
    
    /**
     * Oblicza opłatę za późne wymeldowanie.
     * 
     * @return kwota opłaty za opóźnienie
     */
    public double dajOplateZaPozneWymeldowanie() {
        return SetUp.wymeldowanieKontroler.naliczOplateZaPozneWymeldowanie(
                idRezerwacji, godzinyOpoznienia);
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
     * Zwraca liczbę wymeldowanych rezerwacji.
     * 
     * @return liczba rezerwacji o statusie WYMELDOWANA
     */
    public int dajLiczbeWymeldowanych() {
        return SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.WYMELDOWANA).size();
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
     * Sprawdza czy pokój związany z rezerwacją jest oznaczony jako dostępny.
     * 
     * @return true jeśli pokój jest dostępny (zwolniony)
     */
    public boolean czyPokojDostepny() {
        Optional<Rezerwacja> rezerwacja = SetUp.rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isPresent()) {
            int numerPokoju = rezerwacja.get().getPokoj().getNumer();
            return SetUp.pokojeDAO.czyDostepny(numerPokoju);
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
