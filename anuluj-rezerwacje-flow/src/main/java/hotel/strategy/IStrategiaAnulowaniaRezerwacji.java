package hotel.strategy;

import hotel.model.Rezerwacja;

/**
 * Interfejs strategii anulowania rezerwacji (wzorzec Strategy).
 * Warstwa: STRATEGIA
 * 
 * Pozwala na różne polityki anulowania w zależności od kontekstu
 * (anulowanie przez gościa, przez recepcję, itp.)
 */
public interface IStrategiaAnulowaniaRezerwacji {
    
    /**
     * Oblicza karę za anulowanie rezerwacji.
     * @param rezerwacja rezerwacja do anulowania
     * @return kwota kary (0 jeśli bez kary)
     */
    double obliczKareZaAnulowanie(Rezerwacja rezerwacja);
    
    /**
     * Sprawdza czy rezerwację można anulować według tej strategii.
     * @param rezerwacja rezerwacja do sprawdzenia
     * @return true jeśli można anulować
     */
    boolean czyMoznaAnulowac(Rezerwacja rezerwacja);
    
    /**
     * Zwraca opis strategii anulowania.
     * @return opis polityki anulowania
     */
    String getOpisPolityki();
}
