package hotel.controller;

import hotel.model.IHotelModel;
import hotel.model.Rezerwacja;

import java.util.Optional;

/**
 * Kontroler odpowiedzialny za proces wymeldowania gościa.
 */
public class WymeldowanieKontroler implements IWymeldowanieKontroler {
    
    private static final double STAWKA_ZA_GODZINE = 25.0;
    
    private final IHotelModel model;
    
    /**
     * Konstruktor kontrolera wymeldowania.
     * @param model model hotelu
     */
    public WymeldowanieKontroler(IHotelModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model nie może być null");
        }
        this.model = model;
    }
    
    @Override
    public boolean wymeldujGoscia(int idRezerwacji) {
        if (idRezerwacji <= 0) {
            return false;
        }
        return model.wymeldujGoscia(idRezerwacji);
    }
    
    @Override
    public double naliczOplateZaPozneWymeldowanie(int idRezerwacji, int godzinyOpoznienia) {
        if (idRezerwacji <= 0 || godzinyOpoznienia <= 0) {
            return 0;
        }
        
        Optional<Rezerwacja> rezerwacja = model.znajdzRezerwacje(idRezerwacji);
        if (rezerwacja.isEmpty()) {
            return 0;
        }
        
        // Opłata zależy od liczby godzin opóźnienia
        return godzinyOpoznienia * STAWKA_ZA_GODZINE;
    }
}
