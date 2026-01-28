package hotel.model;

/**
 * Interfejs dodatku do rezerwacji (wzorzec Dekorator).
 * Pozwala na dodawanie dodatkowych usług do rezerwacji.
 */
public interface IDodatek {
    
    /**
     * Oblicza dodatkowy koszt usługi.
     * @return koszt dodatku
     */
    double obliczDodatkowyKoszt();
    
    /**
     * Zwraca opis dodatku.
     * @return opis tekstowy dodatku
     */
    String getOpis();
}
