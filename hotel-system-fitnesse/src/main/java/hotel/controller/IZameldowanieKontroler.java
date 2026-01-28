package hotel.controller;

/**
 * Interfejs kontrolera zameldowania.
 * Definiuje operacje związane z procesem zameldowania gościa.
 */
public interface IZameldowanieKontroler {
    
    /**
     * Zameldowuje gościa na podstawie rezerwacji.
     * @param idRezerwacji ID rezerwacji
     * @return true jeśli zameldowano pomyślnie
     */
    boolean zameldujGoscia(int idRezerwacji);
}
