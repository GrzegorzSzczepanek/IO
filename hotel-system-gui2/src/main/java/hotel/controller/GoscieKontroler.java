package hotel.controller;

import hotel.model.Gosc;
import hotel.model.IHotelModel;

import java.util.Optional;

/**
 * Kontroler zarządzający profilami gości.
 */
public class GoscieKontroler implements IGoscieKontroler {
    
    private final IHotelModel model;
    
    /**
     * Konstruktor kontrolera gości.
     * @param model model hotelu
     */
    public GoscieKontroler(IHotelModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model nie może być null");
        }
        this.model = model;
    }
    
    @Override
    public Optional<Gosc> przegladProfiluGoscia(int idGoscia) {
        if (idGoscia <= 0) {
            return Optional.empty();
        }
        return model.znajdzProfilGoscia(idGoscia);
    }
    
    @Override
    public boolean edytujProfilGoscia(int idGoscia, String nowyEmail) {
        if (idGoscia <= 0 || nowyEmail == null || !nowyEmail.contains("@")) {
            return false;
        }
        
        Optional<Gosc> gosc = model.znajdzProfilGoscia(idGoscia);
        if (gosc.isEmpty()) {
            return false;
        }
        
        try {
            gosc.get().setEmail(nowyEmail);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public Gosc utworzProfilGoscia(String imie, String nazwisko, String email) {
        if (imie == null || imie.trim().isEmpty() ||
            nazwisko == null || nazwisko.trim().isEmpty() ||
            email == null || !email.contains("@")) {
            return null;
        }
        return model.utworzProfilGoscia(imie, nazwisko, email);
    }
}
