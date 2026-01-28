package hotel.testyfitnesse;

import fit.ColumnFixture;
import hotel.model.Rezerwacja;

import java.util.Optional;

/**
 * Klasa testująca przypadek użycia: Anuluj Rezerwację.
 * Rozszerza ColumnFixture - atrybuty publiczne są danymi wejściowymi z tabeli FitNesse.
 * 
 * Testowany przypadek użycia: Anulowanie rezerwacji hotelowej
 * Kontroler: RezerwacjeKontroler.anulujRezerwacje(int idRezerwacji)
 * 
 * @author Grzegorz Szczepanek
 */
public class TestAnulujRezerwacje extends ColumnFixture {
    
    // ========== DANE WEJŚCIOWE (pobierane z tabeli FitNesse) ==========
    
    /** ID rezerwacji do anulowania - wartość pobierana z tabeli testowej */
    public int idRezerwacji;
    
    // ========== OPERACJE TESTUJĄCE ==========
    
    /**
     * Testuje operację anulowania rezerwacji.
     * Wywołuje operację realizującą przypadek użycia i sprawdza wynik.
     * 
     * @return true jeśli anulowanie się powiodło, false w przeciwnym razie
     */
    public boolean anulujRezerwacje() {
        // 1. Zapisanie stanu przed operacją
        int liczbaPrzed = dajLiczbeRezerwacjiAktywnych();
        String statusPrzed = dajStatusRezerwacji();
        
        // 2. Wykonanie testowanej operacji (przypadek użycia)
        boolean wynik = SetUp.rezerwacjeKontroler.anulujRezerwacje(idRezerwacji);
        
        // 3. Zapisanie stanu po operacji
        int liczbaPo = dajLiczbeRezerwacjiAktywnych();
        String statusPo = dajStatusRezerwacji();
        
        // 4. Zwrócenie wyniku operacji
        return wynik;
    }
    
    /**
     * Testuje operację anulowania rezerwacji z podaniem przyczyny.
     * 
     * @return true jeśli anulowanie się powiodło
     */
    public boolean anulujRezerwacjeZPrzyczyna() {
        return SetUp.rezerwacjeKontroler.anulujRezerwacje(idRezerwacji, "Rezygnacja klienta");
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
     * Zwraca liczbę aktywnych rezerwacji (nie anulowanych i nie wymeldowanych).
     * 
     * @return liczba aktywnych rezerwacji
     */
    public int dajLiczbeRezerwacjiAktywnych() {
        return SetUp.rezerwacjeDAO.pobierzAktywne().size();
    }
    
    /**
     * Zwraca liczbę anulowanych rezerwacji.
     * 
     * @return liczba anulowanych rezerwacji
     */
    public int dajLiczbeRezerwacjiAnulowanych() {
        return SetUp.rezerwacjeDAO.pobierzPoStatusie(Rezerwacja.Status.ANULOWANA).size();
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
     * Zwraca opłatę za anulowanie rezerwacji.
     * 
     * @return kwota opłaty za anulowanie
     */
    public double dajOplateZaAnulowanie() {
        return SetUp.rezerwacjeKontroler.pobierzOplateZaAnulowanie(idRezerwacji);
    }
}
