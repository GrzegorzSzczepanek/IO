package hotel.controller;

import hotel.model.IHotelModel;

/**
 * Kontroler odpowiedzialny za proces zameldowania gościa.
 */
public class ZameldowanieKontroler implements IZameldowanieKontroler {
    
    private final IHotelModel model;
    
    /**
     * Konstruktor kontrolera zameldowania.
     * @param model model hotelu
     */
    public ZameldowanieKontroler(IHotelModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model nie może być null");
        }
        this.model = model;
    }
    
    @Override
    public boolean zameldujGoscia(int idRezerwacji) {
        if (idRezerwacji <= 0) {
            return false;
        }
        return model.zameldujGoscia(idRezerwacji);
    }
}
