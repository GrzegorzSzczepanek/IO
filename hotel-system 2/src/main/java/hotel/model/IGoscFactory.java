package hotel.model;

/**
 * Interfejs fabryki gości (wzorzec Factory).
 * Definiuje metodę tworzenia nowych obiektów gości.
 */
public interface IGoscFactory {
    
    /**
     * Tworzy nowego gościa.
     * @param imie imię gościa
     * @param nazwisko nazwisko gościa
     * @param email adres email gościa
     * @return nowy obiekt gościa
     */
    Gosc utworzGoscia(String imie, String nazwisko, String email);
}
