package hotel.factory;

import hotel.model.Gosc;

/**
 * Implementacja fabryki gości.
 */
public class FabrykaGosci implements IGoscFactory {
    
    @Override
    public Gosc utworzGoscia(String imie, String nazwisko, String email) {
        return new Gosc(imie, nazwisko, email);
    }
}
