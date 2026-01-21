package hotel.controller;

import hotel.model.Rezerwacja;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfejs kontrolera rezerwacji.
 * Warstwa: KONTROLER
 * 
 * Definiuje operacje dostępne dla warstwy prezentacji.
 */
public interface IRezerwacjeKontroler {
    
    /**
     * Tworzy nową rezerwację.
     * @param goscId ID gościa
     * @param pokojNumer numer pokoju
     * @param dataOd data rozpoczęcia
     * @param dataDo data zakończenia
     * @return utworzona rezerwacja
     */
    Rezerwacja utworzRezerwacje(int goscId, int pokojNumer, LocalDate dataOd, LocalDate dataDo);
    
    /**
     * Anuluje rezerwację.
     * @param rezerwacjaId ID rezerwacji
     * @return true jeśli anulowano pomyślnie
     */
    boolean anulujRezerwacje(int rezerwacjaId);
    
    /**
     * Modyfikuje rezerwację.
     * @param rezerwacjaId ID rezerwacji
     * @param nowaDataOd nowa data rozpoczęcia
     * @param nowaDataDo nowa data zakończenia
     * @return zmodyfikowana rezerwacja
     */
    Rezerwacja modyfikujRezerwacje(int rezerwacjaId, LocalDate nowaDataOd, LocalDate nowaDataDo);
    
    /**
     * Przegląda zarezerwowane pokoje w danym okresie.
     * @param dataOd data początkowa
     * @param dataDo data końcowa
     * @return lista rezerwacji
     */
    List<Rezerwacja> przegladajZarezerwowanePokoje(LocalDate dataOd, LocalDate dataDo);
    
    /**
     * Pobiera opłatę za anulowanie (stała 20%).
     * @param rezerwacjaId ID rezerwacji
     * @return kwota opłaty
     */
    double pobierzOplateZaAnulowanie(int rezerwacjaId);
    
    /**
     * Oblicza opłatę za anulowanie z uwzględnieniem terminu.
     * @param rezerwacjaId ID rezerwacji
     * @return kwota opłaty zależna od terminu
     */
    double obliczOplateZaAnulowanieZTerminem(int rezerwacjaId);
    
    /**
     * Pobiera rezerwację po ID.
     * @param rezerwacjaId ID rezerwacji
     * @return rezerwacja lub null
     */
    Rezerwacja pobierzRezerwacje(int rezerwacjaId);
}

