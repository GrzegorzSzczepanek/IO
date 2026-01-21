package hotel.strategy;

import hotel.model.Rezerwacja;

/**
 * Strategia anulowania rezerwacji przez gościa.
 * Warstwa: STRATEGIA
 * 
 * Polityka kar:
 * - Więcej niż 7 dni przed: 0% kary
 * - 3-7 dni przed: 20% kary
 * - 1-3 dni przed: 50% kary
 * - Mniej niż 1 dzień: 100% kary
 */
public class AnulowaniePrzezGoscia implements IStrategiaAnulowaniaRezerwacji {
    
    // Progi czasowe (w dniach)
    private static final long PROG_BEZ_KARY = 7;
    private static final long PROG_MALA_KARA = 3;
    private static final long PROG_SREDNIA_KARA = 1;
    
    // Procenty kar
    private static final double KARA_BEZ = 0.0;
    private static final double KARA_MALA = 0.20;      // 20%
    private static final double KARA_SREDNIA = 0.50;   // 50%
    private static final double KARA_PELNA = 1.00;     // 100%
    
    @Override
    public double obliczKareZaAnulowanie(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            return 0.0;
        }
        
        double cenaRezerwacji = rezerwacja.obliczCene();
        long dniDoRozpoczecia = rezerwacja.dniDoRozpoczecia();
        
        double procentKary;
        
        if (dniDoRozpoczecia > PROG_BEZ_KARY) {
            // Więcej niż 7 dni - bez kary
            procentKary = KARA_BEZ;
        } else if (dniDoRozpoczecia > PROG_MALA_KARA) {
            // 3-7 dni - 20% kary
            procentKary = KARA_MALA;
        } else if (dniDoRozpoczecia > PROG_SREDNIA_KARA) {
            // 1-3 dni - 50% kary
            procentKary = KARA_SREDNIA;
        } else {
            // Mniej niż 1 dzień - 100% kary
            procentKary = KARA_PELNA;
        }
        
        return cenaRezerwacji * procentKary;
    }
    
    @Override
    public boolean czyMoznaAnulowac(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            return false;
        }
        
        // Gość może anulować tylko rezerwacje NOWE lub POTWIERDZONE
        return rezerwacja.czyMoznaAnulowac();
    }
    
    @Override
    public String getOpisPolityki() {
        return "Anulowanie przez gościa:\n" +
               "- Powyżej 7 dni przed przyjazdem: bez opłat\n" +
               "- 3-7 dni przed przyjazdem: 20% wartości rezerwacji\n" +
               "- 1-3 dni przed przyjazdem: 50% wartości rezerwacji\n" +
               "- Poniżej 1 dnia: 100% wartości rezerwacji";
    }
    
    /**
     * Zwraca procent kary dla danej liczby dni.
     * Metoda pomocnicza do testów.
     * @param dniDoRozpoczecia liczba dni do rozpoczęcia rezerwacji
     * @return procent kary (0.0 - 1.0)
     */
    public double getProcentKary(long dniDoRozpoczecia) {
        if (dniDoRozpoczecia > PROG_BEZ_KARY) {
            return KARA_BEZ;
        } else if (dniDoRozpoczecia > PROG_MALA_KARA) {
            return KARA_MALA;
        } else if (dniDoRozpoczecia > PROG_SREDNIA_KARA) {
            return KARA_SREDNIA;
        } else {
            return KARA_PELNA;
        }
    }
}
