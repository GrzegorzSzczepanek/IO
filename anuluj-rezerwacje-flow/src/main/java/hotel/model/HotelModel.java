package hotel.model;

import hotel.dao.GoscieDAO;
import hotel.dao.PokojeDAO;
import hotel.dao.RezerwacjeDAO;
import hotel.factory.IGoscFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementacja modelu hotelowego.
 * Warstwa: MODEL
 * 
 * Główna logika biznesowa systemu - koordynuje operacje między DAO.
 */
public class HotelModel implements IHotelModel {
    
    private final RezerwacjeDAO rezerwacjeDAO;
    private final PokojeDAO pokojeDAO;
    private final GoscieDAO goscieDAO;
    private final IGoscFactory fabrykaGosci;
    
    /**
     * Konstruktor z wstrzykiwaniem zależności (Dependency Injection).
     */
    public HotelModel(RezerwacjeDAO rezerwacjeDAO, PokojeDAO pokojeDAO, 
                      GoscieDAO goscieDAO, IGoscFactory fabrykaGosci) {
        this.rezerwacjeDAO = rezerwacjeDAO;
        this.pokojeDAO = pokojeDAO;
        this.goscieDAO = goscieDAO;
        this.fabrykaGosci = fabrykaGosci;
    }
    
    // ========== OPERACJE NA REZERWACJACH ==========
    
    @Override
    public Rezerwacja utworzRezerwacje(int goscId, int pokojNumer, 
                                        LocalDate dataOd, LocalDate dataDo) {
        // Walidacja dat
        if (dataOd == null || dataDo == null) {
            throw new IllegalArgumentException("Daty nie mogą być null");
        }
        if (!dataOd.isBefore(dataDo)) {
            throw new IllegalArgumentException("Data rozpoczęcia musi być przed datą zakończenia");
        }
        if (dataOd.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data rozpoczęcia nie może być w przeszłości");
        }
        
        // Sprawdź czy gość istnieje
        Gosc gosc = goscieDAO.pobierz(goscId);
        if (gosc == null) {
            throw new IllegalArgumentException("Gość o ID " + goscId + " nie istnieje");
        }
        
        // Sprawdź czy pokój istnieje
        Pokoj pokoj = pokojeDAO.pobierz(pokojNumer);
        if (pokoj == null) {
            throw new IllegalArgumentException("Pokój o numerze " + pokojNumer + " nie istnieje");
        }
        
        // Sprawdź dostępność pokoju
        if (!rezerwacjeDAO.czyPokojDostepny(pokojNumer, dataOd, dataDo)) {
            throw new IllegalStateException("Pokój " + pokojNumer + 
                    " jest niedostępny w wybranym terminie");
        }
        
        // Utwórz rezerwację
        Rezerwacja rezerwacja = new Rezerwacja(0, dataOd, dataDo, pokoj, gosc);
        rezerwacjeDAO.zapisz(rezerwacja);
        
        return rezerwacja;
    }
    
    @Override
    public boolean anulujRezerwacje(int rezerwacjaId) {
        Rezerwacja rezerwacja = rezerwacjeDAO.pobierz(rezerwacjaId);
        
        if (rezerwacja == null) {
            return false;
        }
        
        // Sprawdź czy można anulować
        if (!rezerwacja.czyMoznaAnulowac()) {
            return false;
        }
        
        // Anuluj rezerwację
        rezerwacja.setStatus(Rezerwacja.StatusRezerwacji.ANULOWANA);
        rezerwacjeDAO.aktualizuj(rezerwacja);
        
        return true;
    }
    
    @Override
    public Rezerwacja modyfikujRezerwacje(int rezerwacjaId, 
                                           LocalDate nowaDataOd, LocalDate nowaDataDo) {
        Rezerwacja rezerwacja = rezerwacjeDAO.pobierz(rezerwacjaId);
        
        if (rezerwacja == null) {
            return null;
        }
        
        // Sprawdź czy można modyfikować
        if (!rezerwacja.czyMoznaAnulowac()) {
            return null;
        }
        
        // Walidacja nowych dat
        if (nowaDataOd == null || nowaDataDo == null) {
            throw new IllegalArgumentException("Daty nie mogą być null");
        }
        if (!nowaDataOd.isBefore(nowaDataDo)) {
            throw new IllegalArgumentException("Data rozpoczęcia musi być przed datą zakończenia");
        }
        
        // Sprawdź dostępność (wyłączając bieżącą rezerwację)
        int pokojNumer = rezerwacja.getPokoj().getNumer();
        List<Rezerwacja> kolidujace = rezerwacjeDAO.pobierzDlaPokoju(pokojNumer).stream()
                .filter(r -> r.getId() != rezerwacjaId)
                .filter(r -> r.getStatus() != Rezerwacja.StatusRezerwacji.ANULOWANA)
                .filter(r -> !r.getDataDo().isBefore(nowaDataOd) && !r.getDataOd().isAfter(nowaDataDo))
                .collect(Collectors.toList());
        
        if (!kolidujace.isEmpty()) {
            throw new IllegalStateException("Pokój jest niedostępny w nowym terminie");
        }
        
        // Aktualizuj daty
        rezerwacja.setDataOd(nowaDataOd);
        rezerwacja.setDataDo(nowaDataDo);
        rezerwacjeDAO.aktualizuj(rezerwacja);
        
        return rezerwacja;
    }
    
    @Override
    public List<Rezerwacja> znajdzRezerwacje(LocalDate dataOd, LocalDate dataDo) {
        if (dataOd == null && dataDo == null) {
            return rezerwacjeDAO.pobierzWszystkie();
        }
        
        LocalDate od = dataOd != null ? dataOd : LocalDate.MIN;
        LocalDate doData = dataDo != null ? dataDo : LocalDate.MAX;
        
        return rezerwacjeDAO.pobierzWOkresie(od, doData);
    }
    
    @Override
    public Rezerwacja pobierzRezerwacje(int rezerwacjaId) {
        return rezerwacjeDAO.pobierz(rezerwacjaId);
    }
    
    // ========== OPERACJE NA POKOJACH ==========
    
    @Override
    public List<Pokoj> znajdzDostepnePokoje(LocalDate dataOd, LocalDate dataDo) {
        if (dataOd == null || dataDo == null) {
            throw new IllegalArgumentException("Daty nie mogą być null");
        }
        
        return pokojeDAO.pobierzWszystkie().stream()
                .filter(p -> p.getStatus() == Pokoj.StatusPokoju.DOSTEPNY)
                .filter(p -> rezerwacjeDAO.czyPokojDostepny(p.getNumer(), dataOd, dataDo))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean aktualizujStatusPokoju(int pokojNumer, Pokoj.StatusPokoju nowyStatus) {
        Pokoj pokoj = pokojeDAO.pobierz(pokojNumer);
        
        if (pokoj == null) {
            return false;
        }
        
        pokoj.setStatus(nowyStatus);
        pokojeDAO.aktualizuj(pokoj);
        
        return true;
    }
    
    // ========== OPERACJE NA GOŚCIACH ==========
    
    @Override
    public Gosc utworzProfilGoscia(String imie, String nazwisko, String email) {
        // Sprawdź czy email już istnieje
        Gosc istniejacy = goscieDAO.znajdzPoEmail(email);
        if (istniejacy != null) {
            throw new IllegalStateException("Gość z emailem " + email + " już istnieje");
        }
        
        Gosc gosc = fabrykaGosci.utworzGoscia(imie, nazwisko, email);
        goscieDAO.zapisz(gosc);
        
        return gosc;
    }
    
    @Override
    public Gosc znajdzProfilGoscia(int goscId) {
        return goscieDAO.pobierz(goscId);
    }
    
    // ========== OPERACJE FINANSOWE ==========
    
    @Override
    public double pobierzOplate(int rezerwacjaId) {
        Rezerwacja rezerwacja = rezerwacjeDAO.pobierz(rezerwacjaId);
        
        if (rezerwacja == null) {
            throw new IllegalArgumentException("Rezerwacja o ID " + rezerwacjaId + " nie istnieje");
        }
        
        return rezerwacja.obliczCene();
    }
    
    @Override
    public boolean potwierdzPlatnosc(int rezerwacjaId) {
        Rezerwacja rezerwacja = rezerwacjeDAO.pobierz(rezerwacjaId);
        
        if (rezerwacja == null) {
            return false;
        }
        
        if (rezerwacja.getStatus() == Rezerwacja.StatusRezerwacji.NOWA) {
            rezerwacja.setStatus(Rezerwacja.StatusRezerwacji.POTWIERDZONA);
            rezerwacjeDAO.aktualizuj(rezerwacja);
            return true;
        }
        
        return false;
    }
    
    // ========== OPERACJE ZAMELDOWANIA/WYMELDOWANIA ==========
    
    @Override
    public boolean zameldujGoscia(int rezerwacjaId) {
        Rezerwacja rezerwacja = rezerwacjeDAO.pobierz(rezerwacjaId);
        
        if (rezerwacja == null) {
            return false;
        }
        
        // Można zameldować tylko potwierdzoną rezerwację
        if (rezerwacja.getStatus() != Rezerwacja.StatusRezerwacji.POTWIERDZONA) {
            return false;
        }
        
        // Zmień status rezerwacji
        rezerwacja.setStatus(Rezerwacja.StatusRezerwacji.ZAMELDOWANA);
        rezerwacjeDAO.aktualizuj(rezerwacja);
        
        // Zmień status pokoju
        if (rezerwacja.getPokoj() != null) {
            rezerwacja.getPokoj().setStatus(Pokoj.StatusPokoju.ZAJETY);
            pokojeDAO.aktualizuj(rezerwacja.getPokoj());
        }
        
        return true;
    }
    
    @Override
    public boolean wymeldujGoscia(int rezerwacjaId) {
        Rezerwacja rezerwacja = rezerwacjeDAO.pobierz(rezerwacjaId);
        
        if (rezerwacja == null) {
            return false;
        }
        
        // Można wymeldować tylko zameldowaną rezerwację
        if (rezerwacja.getStatus() != Rezerwacja.StatusRezerwacji.ZAMELDOWANA) {
            return false;
        }
        
        // Zmień status rezerwacji
        rezerwacja.setStatus(Rezerwacja.StatusRezerwacji.WYMELDOWANA);
        rezerwacjeDAO.aktualizuj(rezerwacja);
        
        // Zmień status pokoju na "do sprzątania"
        if (rezerwacja.getPokoj() != null) {
            rezerwacja.getPokoj().setStatus(Pokoj.StatusPokoju.W_SPRZATANIU);
            pokojeDAO.aktualizuj(rezerwacja.getPokoj());
        }
        
        return true;
    }
}
