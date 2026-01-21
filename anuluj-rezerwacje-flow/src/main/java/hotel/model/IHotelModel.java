package hotel.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfejs modelu hotelowego.
 * Warstwa: MODEL
 * 
 * Definiuje główne operacje biznesowe systemu hotelowego.
 */
public interface IHotelModel {
    
    // ========== OPERACJE NA REZERWACJACH ==========
    
    /**
     * Tworzy nową rezerwację.
     * @param goscId ID gościa
     * @param pokojNumer numer pokoju
     * @param dataOd data rozpoczęcia
     * @param dataDo data zakończenia
     * @return utworzona rezerwacja
     * @throws IllegalArgumentException jeśli dane są nieprawidłowe
     * @throws IllegalStateException jeśli pokój jest niedostępny
     */
    Rezerwacja utworzRezerwacje(int goscId, int pokojNumer, LocalDate dataOd, LocalDate dataDo);
    
    /**
     * Anuluje rezerwację.
     * @param rezerwacjaId ID rezerwacji
     * @return true jeśli anulowano pomyślnie
     */
    boolean anulujRezerwacje(int rezerwacjaId);
    
    /**
     * Modyfikuje daty rezerwacji.
     * @param rezerwacjaId ID rezerwacji
     * @param nowaDataOd nowa data rozpoczęcia
     * @param nowaDataDo nowa data zakończenia
     * @return zmodyfikowana rezerwacja lub null jeśli nie można zmodyfikować
     */
    Rezerwacja modyfikujRezerwacje(int rezerwacjaId, LocalDate nowaDataOd, LocalDate nowaDataDo);
    
    /**
     * Znajduje rezerwacje w danym okresie.
     * @param dataOd data początkowa (null = bez ograniczenia)
     * @param dataDo data końcowa (null = bez ograniczenia)
     * @return lista rezerwacji
     */
    List<Rezerwacja> znajdzRezerwacje(LocalDate dataOd, LocalDate dataDo);
    
    /**
     * Pobiera rezerwację po ID.
     * @param rezerwacjaId ID rezerwacji
     * @return rezerwacja lub null
     */
    Rezerwacja pobierzRezerwacje(int rezerwacjaId);
    
    // ========== OPERACJE NA POKOJACH ==========
    
    /**
     * Znajduje dostępne pokoje w danym terminie.
     * @param dataOd data rozpoczęcia
     * @param dataDo data zakończenia
     * @return lista dostępnych pokoi
     */
    List<Pokoj> znajdzDostepnePokoje(LocalDate dataOd, LocalDate dataDo);
    
    /**
     * Aktualizuje status pokoju.
     * @param pokojNumer numer pokoju
     * @param nowyStatus nowy status
     * @return true jeśli zaktualizowano
     */
    boolean aktualizujStatusPokoju(int pokojNumer, Pokoj.StatusPokoju nowyStatus);
    
    // ========== OPERACJE NA GOŚCIACH ==========
    
    /**
     * Tworzy profil gościa.
     * @param imie imię
     * @param nazwisko nazwisko
     * @param email email
     * @return utworzony gość
     */
    Gosc utworzProfilGoscia(String imie, String nazwisko, String email);
    
    /**
     * Znajduje profil gościa po ID.
     * @param goscId ID gościa
     * @return gość lub null
     */
    Gosc znajdzProfilGoscia(int goscId);
    
    // ========== OPERACJE FINANSOWE ==========
    
    /**
     * Pobiera opłatę za rezerwację.
     * @param rezerwacjaId ID rezerwacji
     * @return kwota do zapłaty
     */
    double pobierzOplate(int rezerwacjaId);
    
    /**
     * Potwierdza płatność za rezerwację.
     * @param rezerwacjaId ID rezerwacji
     * @return true jeśli potwierdzono
     */
    boolean potwierdzPlatnosc(int rezerwacjaId);
    
    // ========== OPERACJE ZAMELDOWANIA/WYMELDOWANIA ==========
    
    /**
     * Zameldowuje gościa.
     * @param rezerwacjaId ID rezerwacji
     * @return true jeśli zameldowano
     */
    boolean zameldujGoscia(int rezerwacjaId);
    
    /**
     * Wymeldowuje gościa.
     * @param rezerwacjaId ID rezerwacji
     * @return true jeśli wymeldowano
     */
    boolean wymeldujGoscia(int rezerwacjaId);
}
