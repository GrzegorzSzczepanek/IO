package hotel.controller;

import hotel.model.Gosc;
import hotel.model.IHotelModel;
import hotel.model.Pokoj;
import hotel.model.Rezerwacja;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Kontroler zarządzający rezerwacjami.
 * Implementuje operacje związane z tworzeniem, modyfikacją i anulowaniem rezerwacji.
 */
public class RezerwacjeKontroler implements IRezerwacjeKontroler {
    
    private final IHotelModel model;
    
    /**
     * Konstruktor kontrolera rezerwacji.
     * @param model model hotelu
     */
    public RezerwacjeKontroler(IHotelModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model nie może być null");
        }
        this.model = model;
    }
    
    @Override
    public Rezerwacja utworzRezerwacje(Gosc gosc, Pokoj pokoj, LocalDate dataOd, LocalDate dataDo) {
        if (gosc == null || pokoj == null || dataOd == null || dataDo == null) {
            throw new IllegalArgumentException("Parametry nie mogą być null");
        }
        if (dataOd.isAfter(dataDo)) {
            throw new IllegalArgumentException("Data rozpoczęcia nie może być późniejsza niż data zakończenia");
        }
        return model.utworzRezerwacje(gosc, pokoj, dataOd, dataDo);
    }
    
    @Override
    public boolean anulujRezerwacje(int idRezerwacji) {
        if (idRezerwacji <= 0) {
            return false;
        }
        return model.anulujRezerwacje(idRezerwacji);
    }
    
    @Override
    public boolean modyfikujRezerwacje(int idRezerwacji, LocalDate nowaDataOd, LocalDate nowaDataDo) {
        if (idRezerwacji <= 0 || nowaDataOd == null || nowaDataDo == null) {
            return false;
        }
        if (nowaDataOd.isAfter(nowaDataDo)) {
            return false;
        }
        return model.modyfikujRezerwacje(idRezerwacji, nowaDataOd, nowaDataDo);
    }
    
    @Override
    public List<Rezerwacja> przegladajZarezerwowanePokoje() {
        // Pobiera wszystkie aktywne rezerwacje poprzez model
        // W rzeczywistej implementacji wymagałoby to dodatkowej metody w IHotelModel
        return new ArrayList<>();
    }
    
    @Override
    public double pobierzOplateZaAnulowanie(int idRezerwacji) {
        if (idRezerwacji <= 0) {
            return 0;
        }
        return model.pobierzOplate(idRezerwacji);
    }
}
