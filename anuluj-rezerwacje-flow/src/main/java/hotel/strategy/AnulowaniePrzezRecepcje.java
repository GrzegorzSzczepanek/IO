package hotel.strategy;

import hotel.model.Rezerwacja;

/**
 * Strategia anulowania rezerwacji przez recepcję.
 * Warstwa: STRATEGIA
 * 
 * Polityka:
 * - Brak kary dla gościa
 * - Można anulować w każdym momencie (nawet zameldowane)
 */
public class AnulowaniePrzezRecepcje implements IStrategiaAnulowaniaRezerwacji {
    
    @Override
    public double obliczKareZaAnulowanie(Rezerwacja rezerwacja) {
        // Recepcja anuluje bez kary dla gościa
        return 0.0;
    }
    
    @Override
    public boolean czyMoznaAnulowac(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            return false;
        }
        
        // Recepcja może anulować wszystko oprócz już wymeldowanych i anulowanych
        Rezerwacja.StatusRezerwacji status = rezerwacja.getStatus();
        return status != Rezerwacja.StatusRezerwacji.WYMELDOWANA &&
               status != Rezerwacja.StatusRezerwacji.ANULOWANA;
    }
    
    @Override
    public String getOpisPolityki() {
        return "Anulowanie przez recepcję:\n" +
               "- Bez opłat dla gościa\n" +
               "- Możliwe w każdym momencie przed wymeldowaniem";
    }
}
