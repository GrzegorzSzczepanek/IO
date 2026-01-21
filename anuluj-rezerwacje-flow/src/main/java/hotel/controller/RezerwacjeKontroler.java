package hotel.controller;

import hotel.model.IHotelModel;
import hotel.model.Rezerwacja;
import hotel.strategy.IStrategiaAnulowaniaRezerwacji;
import hotel.strategy.AnulowaniePrzezGoscia;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementacja kontrolera rezerwacji.
 * Warstwa: KONTROLER
 * 
 * Pośredniczy między warstwą prezentacji (UseCase) a modelem.
 * Wykorzystuje strategię anulowania do obliczania kar.
 */
public class RezerwacjeKontroler implements IRezerwacjeKontroler {
    
    /** Stały procent kary za anulowanie (metoda uproszczona) */
    private static final double PROCENT_KARY_ZA_ANULOWANIE = 0.20;
    
    private final IHotelModel model;
    private final IStrategiaAnulowaniaRezerwacji strategiaAnulowania;
    
    /**
     * Konstruktor z domyślną strategią anulowania przez gościa.
     * @param model model hotelowy
     */
    public RezerwacjeKontroler(IHotelModel model) {
        this(model, new AnulowaniePrzezGoscia());
    }
    
    /**
     * Konstruktor z możliwością podania strategii anulowania.
     * @param model model hotelowy
     * @param strategiaAnulowania strategia anulowania
     */
    public RezerwacjeKontroler(IHotelModel model, 
                               IStrategiaAnulowaniaRezerwacji strategiaAnulowania) {
        this.model = model;
        this.strategiaAnulowania = strategiaAnulowania;
    }
    
    @Override
    public Rezerwacja utworzRezerwacje(int goscId, int pokojNumer, 
                                        LocalDate dataOd, LocalDate dataDo) {
        return model.utworzRezerwacje(goscId, pokojNumer, dataOd, dataDo);
    }
    
    @Override
    public boolean anulujRezerwacje(int rezerwacjaId) {
        // Sprawdź czy można anulować według strategii
        Rezerwacja rezerwacja = model.pobierzRezerwacje(rezerwacjaId);
        
        if (rezerwacja == null) {
            return false;
        }
        
        if (!strategiaAnulowania.czyMoznaAnulowac(rezerwacja)) {
            return false;
        }
        
        return model.anulujRezerwacje(rezerwacjaId);
    }
    
    @Override
    public Rezerwacja modyfikujRezerwacje(int rezerwacjaId, 
                                           LocalDate nowaDataOd, LocalDate nowaDataDo) {
        return model.modyfikujRezerwacje(rezerwacjaId, nowaDataOd, nowaDataDo);
    }
    
    @Override
    public List<Rezerwacja> przegladajZarezerwowanePokoje(LocalDate dataOd, LocalDate dataDo) {
        return model.znajdzRezerwacje(dataOd, dataDo);
    }
    
    @Override
    public double pobierzOplateZaAnulowanie(int rezerwacjaId) {
        // Metoda uproszczona - stałe 20%
        double cenaRezerwacji = model.pobierzOplate(rezerwacjaId);
        return cenaRezerwacji * PROCENT_KARY_ZA_ANULOWANIE;
    }
    
    @Override
    public double obliczOplateZaAnulowanieZTerminem(int rezerwacjaId) {
        // Metoda z wykorzystaniem strategii - kara zależna od terminu
        Rezerwacja rezerwacja = model.pobierzRezerwacje(rezerwacjaId);
        
        if (rezerwacja == null) {
            throw new IllegalArgumentException("Rezerwacja o ID " + rezerwacjaId + " nie istnieje");
        }
        
        return strategiaAnulowania.obliczKareZaAnulowanie(rezerwacja);
    }
    
    @Override
    public Rezerwacja pobierzRezerwacje(int rezerwacjaId) {
        return model.pobierzRezerwacje(rezerwacjaId);
    }
    
    /**
     * Zwraca opis polityki anulowania.
     * @return opis polityki
     */
    public String getOpisPolitykiAnulowania() {
        return strategiaAnulowania.getOpisPolityki();
    }
    
    /**
     * Sprawdza czy rezerwację można anulować.
     * @param rezerwacjaId ID rezerwacji
     * @return true jeśli można anulować
     */
    public boolean czyMoznaAnulowac(int rezerwacjaId) {
        Rezerwacja rezerwacja = model.pobierzRezerwacje(rezerwacjaId);
        
        if (rezerwacja == null) {
            return false;
        }
        
        return strategiaAnulowania.czyMoznaAnulowac(rezerwacja);
    }
}
