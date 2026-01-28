package hotel.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfejs modelu hotelu.
 * Definiuje główne operacje biznesowe systemu zarządzania hotelem.
 */
public interface IHotelModel {
    
    /**
     * Aktualizuje status pokoju.
     * @param numerPokoju numer pokoju
     * @param dostepny czy pokój jest dostępny
     * @return true jeśli operacja się powiodła
     */
    boolean aktualizujStatusPokoju(int numerPokoju, boolean dostepny);
    
    /**
     * Tworzy nową rezerwację.
     * @param gosc gość dokonujący rezerwacji
     * @param pokoj rezerwowany pokój
     * @param dataOd data rozpoczęcia
     * @param dataDo data zakończenia
     * @return utworzona rezerwacja lub null w przypadku błędu
     */
    Rezerwacja utworzRezerwacje(Gosc gosc, Pokoj pokoj, LocalDate dataOd, LocalDate dataDo);
    
    /**
     * Tworzy profil gościa.
     * @param imie imię gościa
     * @param nazwisko nazwisko gościa
     * @param email adres email
     * @return utworzony gość
     */
    Gosc utworzProfilGoscia(String imie, String nazwisko, String email);
    
    /**
     * Anuluje rezerwację.
     * @param idRezerwacji ID rezerwacji do anulowania
     * @return true jeśli anulowano pomyślnie
     */
    boolean anulujRezerwacje(int idRezerwacji);

    /**
     * Anuluje rezerwację z przyczyną.
     * @param idRezerwacji ID rezerwacji do anulowania
     * @param przyczyna przyczyna anulowania
     * @return true jeśli anulowano pomyślnie
     */
    boolean anulujRezerwacje(int idRezerwacji, String przyczyna);
    
    /**
     * Modyfikuje daty rezerwacji.
     * @param idRezerwacji ID rezerwacji
     * @param nowaDataOd nowa data rozpoczęcia
     * @param nowaDataDo nowa data zakończenia
     * @return true jeśli zmodyfikowano pomyślnie
     */
    boolean modyfikujRezerwacje(int idRezerwacji, LocalDate nowaDataOd, LocalDate nowaDataDo);
    
    /**
     * Pobiera opłatę za anulowanie rezerwacji.
     * @param idRezerwacji ID rezerwacji
     * @return kwota opłaty
     */
    double pobierzOplate(int idRezerwacji);
    
    /**
     * Znajduje profil gościa po ID.
     * @param idGoscia ID gościa
     * @return Optional z gościem
     */
    Optional<Gosc> znajdzProfilGoscia(int idGoscia);
    
    /**
     * Znajduje rezerwację po ID.
     * @param idRezerwacji ID rezerwacji
     * @return Optional z rezerwacją
     */
    Optional<Rezerwacja> znajdzRezerwacje(int idRezerwacji);
    
    /**
     * Znajduje dostępne pokoje w danym przedziale dat.
     * @param dataOd data rozpoczęcia
     * @param dataDo data zakończenia
     * @return lista dostępnych pokoi
     */
    List<Pokoj> znajdzDostepnePokoje(LocalDate dataOd, LocalDate dataDo);
    
    /**
     * Zameldowuje gościa.
     * @param idRezerwacji ID rezerwacji
     * @return true jeśli zameldowano pomyślnie
     */
    boolean zameldujGoscia(int idRezerwacji);
    
    /**
     * Wymeldowuje gościa.
     * @param idRezerwacji ID rezerwacji
     * @return true jeśli wymeldowano pomyślnie
     */
    boolean wymeldujGoscia(int idRezerwacji);
    
    /**
     * Potwierdza płatność za rezerwację.
     * @param idRezerwacji ID rezerwacji
     * @return true jeśli płatność została potwierdzona
     */
    boolean potwierdzPlatnosc(int idRezerwacji);
}
