package hotel.factory;

import hotel.model.Gosc;

/**
 * Interfejs fabryki gości.
 */
public interface IGoscFactory {
    Gosc utworzGoscia(String imie, String nazwisko, String email);
}
