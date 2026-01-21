package hotel.controller;

/**
 * Interfejs kontrolera wymeldowania.
 * Definiuje operacje związane z procesem wymeldowania gościa.
 */
public interface IWymeldowanieKontroler {
    
    /**
     * Wymeldowuje gościa.
     * @param idRezerwacji ID rezerwacji
     * @return true jeśli wymeldowano pomyślnie
     */
    boolean wymeldujGoscia(int idRezerwacji);
    
    /**
     * Nalicza opłatę za późne wymeldowanie.
     * @param idRezerwacji ID rezerwacji
     * @param godzinyOpoznienia liczba godzin opóźnienia
     * @return kwota dodatkowej opłaty
     */
    double naliczOplateZaPozneWymeldowanie(int idRezerwacji, int godzinyOpoznienia);
}
