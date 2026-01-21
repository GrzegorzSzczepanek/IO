package hotel.controller;

import hotel.model.Gosc;
import hotel.model.Pokoj;
import hotel.model.Rezerwacja;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfejs kontrolera rezerwacji.
 * Definiuje operacje związane z zarządzaniem rezerwacjami.
 */
public interface IRezerwacjeKontroler {
    
    /**
     * Tworzy nową rezerwację.
     * @param gosc gość
     * @param pokoj pokój
     * @param dataOd data od
     * @param dataDo data do
     * @return utworzona rezerwacja
     */
    Rezerwacja utworzRezerwacje(Gosc gosc, Pokoj pokoj, LocalDate dataOd, LocalDate dataDo);
    
    /**
     * Anuluje rezerwację.
     * @param idRezerwacji ID rezerwacji
     * @return true jeśli anulowano
     */
    boolean anulujRezerwacje(int idRezerwacji);
    
    /**
     * Modyfikuje rezerwację.
     * @param idRezerwacji ID rezerwacji
     * @param nowaDataOd nowa data od
     * @param nowaDataDo nowa data do
     * @return true jeśli zmodyfikowano
     */
    boolean modyfikujRezerwacje(int idRezerwacji, LocalDate nowaDataOd, LocalDate nowaDataDo);
    
    /**
     * Przegląda zarezerwowane pokoje.
     * @return lista rezerwacji
     */
    List<Rezerwacja> przegladajZarezerwowanePokoje();
    
    /**
     * Pobiera opłatę za anulowanie.
     * @param idRezerwacji ID rezerwacji
     * @return kwota opłaty
     */
    double pobierzOplateZaAnulowanie(int idRezerwacji);
}
