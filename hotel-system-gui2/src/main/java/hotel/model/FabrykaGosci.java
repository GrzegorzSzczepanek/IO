package hotel.model;

/**
 * Implementacja fabryki gości (wzorzec Factory).
 * Odpowiada za tworzenie nowych obiektów gości.
 */
public class FabrykaGosci implements IGoscFactory {
    
    @Override
    public Gosc utworzGoscia(String imie, String nazwisko, String email) {
        return new Gosc(imie, nazwisko, email);
    }
    
    /**
     * Tworzy gościa VIP z dodatkowym oznaczeniem.
     * @param imie imię gościa
     * @param nazwisko nazwisko gościa
     * @param email adres email gościa
     * @return nowy obiekt gościa VIP
     */
    public Gosc utworzGosciaVIP(String imie, String nazwisko, String email) {
        // W przyszłości można rozszerzyć o klasę GoscVIP
        return new Gosc(imie, nazwisko, email);
    }
}
