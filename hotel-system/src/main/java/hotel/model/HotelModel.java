package hotel.model;

import hotel.dao.GoscieDAO;
import hotel.dao.PokojeDAO;
import hotel.dao.RezerwacjeDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Główny model systemu zarządzania hotelem.
 * Implementuje logikę biznesową i koordynuje operacje na danych.
 */
public class HotelModel implements IHotelModel {
    
    private static final double WSPOLCZYNNIK_ANULOWANIA = 0.2; // 20% ceny rezerwacji
    
    private final RezerwacjeDAO rezerwacjeDAO;
    private final PokojeDAO pokojeDAO;
    private final GoscieDAO goscieDAO;
    private final IGoscFactory fabryka;
    
    /**
     * Konstruktor tworzący model z własnymi DAO.
     */
    public HotelModel() {
        this.rezerwacjeDAO = new RezerwacjeDAO();
        this.pokojeDAO = new PokojeDAO();
        this.goscieDAO = new GoscieDAO();
        this.fabryka = new FabrykaGosci();
    }
    
    /**
     * Konstruktor z wstrzykiwaniem zależności (do testów).
     * @param rezerwacjeDAO DAO rezerwacji
     * @param pokojeDAO DAO pokoi
     * @param goscieDAO DAO gości
     * @param fabryka fabryka gości
     */
    public HotelModel(RezerwacjeDAO rezerwacjeDAO, PokojeDAO pokojeDAO, 
                      GoscieDAO goscieDAO, IGoscFactory fabryka) {
        this.rezerwacjeDAO = rezerwacjeDAO;
        this.pokojeDAO = pokojeDAO;
        this.goscieDAO = goscieDAO;
        this.fabryka = fabryka;
    }
    
    @Override
    public boolean aktualizujStatusPokoju(int numerPokoju, boolean dostepny) {
        return pokojeDAO.ustawDostepnosc(numerPokoju, dostepny);
    }
    
    @Override
    public Rezerwacja utworzRezerwacje(Gosc gosc, Pokoj pokoj, LocalDate dataOd, LocalDate dataDo) {
        // Sprawdź czy pokój jest dostępny w podanym terminie
        if (!rezerwacjeDAO.czyPokojDostepny(pokoj, dataOd, dataDo)) {
            return null;
        }
        
        // Sprawdź czy gość istnieje
        if (goscieDAO.pobierz(gosc.getId()).isEmpty()) {
            goscieDAO.zapisz(gosc);
        }
        
        // Sprawdź czy pokój istnieje
        if (pokojeDAO.pobierz(pokoj.getNumer()).isEmpty()) {
            pokojeDAO.zapisz(pokoj);
        }
        
        Rezerwacja rezerwacja = new Rezerwacja(dataOd, dataDo, gosc, pokoj);
        return rezerwacjeDAO.zapisz(rezerwacja);
    }
    
    @Override
    public Gosc utworzProfilGoscia(String imie, String nazwisko, String email) {
        // Sprawdź czy gość o takim emailu już istnieje
        if (goscieDAO.istniejeEmail(email)) {
            return goscieDAO.znajdzPoEmail(email).orElse(null);
        }
        
        Gosc nowyGosc = fabryka.utworzGoscia(imie, nazwisko, email);
        return goscieDAO.zapisz(nowyGosc);
    }
    
    @Override
    public boolean anulujRezerwacje(int idRezerwacji) {
        Optional<Rezerwacja> rezerwacja = rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isEmpty()) {
            return false;
        }
        
        Rezerwacja r = rezerwacja.get();
        if (r.getStatus() == Rezerwacja.Status.WYMELDOWANA || 
            r.getStatus() == Rezerwacja.Status.ANULOWANA) {
            return false;
        }
        
        r.setStatus(Rezerwacja.Status.ANULOWANA);
        return rezerwacjeDAO.aktualizuj(r);
    }
    
    @Override
    public boolean modyfikujRezerwacje(int idRezerwacji, LocalDate nowaDataOd, LocalDate nowaDataDo) {
        Optional<Rezerwacja> rezerwacja = rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isEmpty()) {
            return false;
        }
        
        Rezerwacja r = rezerwacja.get();
        
        // Sprawdź czy można modyfikować
        if (r.getStatus() == Rezerwacja.Status.WYMELDOWANA || 
            r.getStatus() == Rezerwacja.Status.ANULOWANA) {
            return false;
        }
        
        // Sprawdź dostępność pokoju w nowym terminie (wykluczając bieżącą rezerwację)
        List<Rezerwacja> kolidujace = rezerwacjeDAO.pobierzDlaPokoju(r.getPokoj()).stream()
                .filter(res -> res.getId() != idRezerwacji)
                .filter(res -> res.getStatus() != Rezerwacja.Status.ANULOWANA)
                .filter(res -> res.getStatus() != Rezerwacja.Status.WYMELDOWANA)
                .filter(res -> koliduja(res.getDataOd(), res.getDataDo(), nowaDataOd, nowaDataDo))
                .toList();
        
        if (!kolidujace.isEmpty()) {
            return false;
        }
        
        r.zmienDaty(nowaDataOd, nowaDataDo);
        return rezerwacjeDAO.aktualizuj(r);
    }
    
    private boolean koliduja(LocalDate od1, LocalDate do1, LocalDate od2, LocalDate do2) {
        return !do1.isBefore(od2) && !od1.isAfter(do2);
    }
    
    @Override
    public double pobierzOplate(int idRezerwacji) {
        Optional<Rezerwacja> rezerwacja = rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isEmpty()) {
            return 0;
        }
        
        return rezerwacja.get().obliczCene() * WSPOLCZYNNIK_ANULOWANIA;
    }
    
    @Override
    public Optional<Gosc> znajdzProfilGoscia(int idGoscia) {
        return goscieDAO.pobierz(idGoscia);
    }
    
    @Override
    public Optional<Rezerwacja> znajdzRezerwacje(int idRezerwacji) {
        return rezerwacjeDAO.pobierz(idRezerwacji);
    }
    
    @Override
    public List<Pokoj> znajdzDostepnePokoje(LocalDate dataOd, LocalDate dataDo) {
        return pokojeDAO.pobierzWszystkie().stream()
                .filter(p -> rezerwacjeDAO.czyPokojDostepny(p, dataOd, dataDo))
                .filter(p -> pokojeDAO.czyDostepny(p.getNumer()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean zameldujGoscia(int idRezerwacji) {
        Optional<Rezerwacja> rezerwacja = rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isEmpty()) {
            return false;
        }
        
        Rezerwacja r = rezerwacja.get();
        if (r.getStatus() != Rezerwacja.Status.NOWA && r.getStatus() != Rezerwacja.Status.POTWIERDZONA) {
            return false;
        }
        
        r.setStatus(Rezerwacja.Status.ZAMELDOWANA);
        pokojeDAO.ustawDostepnosc(r.getPokoj().getNumer(), false);
        return rezerwacjeDAO.aktualizuj(r);
    }
    
    @Override
    public boolean wymeldujGoscia(int idRezerwacji) {
        Optional<Rezerwacja> rezerwacja = rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isEmpty()) {
            return false;
        }
        
        Rezerwacja r = rezerwacja.get();
        if (r.getStatus() != Rezerwacja.Status.ZAMELDOWANA) {
            return false;
        }
        
        r.setStatus(Rezerwacja.Status.WYMELDOWANA);
        pokojeDAO.ustawDostepnosc(r.getPokoj().getNumer(), true);
        return rezerwacjeDAO.aktualizuj(r);
    }
    
    @Override
    public boolean potwierdzPlatnosc(int idRezerwacji) {
        Optional<Rezerwacja> rezerwacja = rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacja.isEmpty()) {
            return false;
        }
        
        Rezerwacja r = rezerwacja.get();
        if (r.getStatus() != Rezerwacja.Status.NOWA) {
            return false;
        }
        
        r.setStatus(Rezerwacja.Status.POTWIERDZONA);
        return rezerwacjeDAO.aktualizuj(r);
    }
    
    // Gettery dla DAO (do testów)
    public RezerwacjeDAO getRezerwacjeDAO() {
        return rezerwacjeDAO;
    }
    
    public PokojeDAO getPokojeDAO() {
        return pokojeDAO;
    }
    
    public GoscieDAO getGoscieDAO() {
        return goscieDAO;
    }
}
