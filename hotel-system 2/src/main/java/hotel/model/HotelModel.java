package hotel.model;

import hotel.dao.GoscieDAO;
import hotel.dao.PokojeDAO;
import hotel.dao.RezerwacjeDAO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Główny model systemu zarządzania hotelem.
 * Implementuje logikę biznesową i koordynuje operacje na danych.
 */
public class HotelModel implements IHotelModel {

    private static final double WSPOLCZYNNIK_ANULOWANIA = 0.2; // 20% ceny rezerwacji
    private static final int DNI_BEZ_OPLATY_ANULOWANIA = 7; // Dni przed przyjazdem, w których anulowanie jest bez opłaty

    // Współczynniki opłat anulowania w zależności od czasu przed przyjazdem
    private static final double WSPOLCZYNNIK_BEZ_OPLATY = 0.0; // Brak opłaty
    private static final double WSPOLCZYNNIK_NISKI = 0.1; // 10% opłaty
    private static final double WSPOLCZYNNIK_SRODKOWY = 0.2; // 20% opłaty
    private static final double WSPOLCZYNNIK_WYSOKI = 0.5; // 50% opłaty

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

        // Ustaw dostępność pokoju po anulowaniu rezerwacji
        pokojeDAO.ustawDostepnosc(r.getPokoj().getNumer(), true);

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

        return obliczOplateAnulowania(rezerwacja.get());
    }

    /**
     * Oblicza opłatę za anulowanie rezerwacji na podstawie czasu przed datą przyjazdu.
     * @param rezerwacja rezerwacja do anulowania
     * @return wysokość opłaty
     */
    private double obliczOplateAnulowania(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            return 0;
        }

        // Oblicz liczbę dni między dzisiejszym dniem a datą przyjazdu
        long dniPrzedPrzyjazdem = ChronoUnit.DAYS.between(LocalDate.now(), rezerwacja.getDataOd());

        // Określ współczynnik opłaty na podstawie czasu przed przyjazdem
        double wspolczynnik;
        if (dniPrzedPrzyjazdem >= DNI_BEZ_OPLATY_ANULOWANIA) {
            wspolczynnik = WSPOLCZYNNIK_BEZ_OPLATY; // Brak opłaty przy anulowaniu wcześnie
        } else if (dniPrzedPrzyjazdem >= 3) {
            wspolczynnik = WSPOLCZYNNIK_NISKI; // Niska opłata przy anulowaniu 3-7 dni przed
        } else if (dniPrzedPrzyjazdem >= 1) {
            wspolczynnik = WSPOLCZYNNIK_SRODKOWY; // Średnia opłata przy anulowaniu 1-3 dni przed
        } else {
            wspolczynnik = WSPOLCZYNNIK_WYSOKI; // Wysoka opłata przy anulowaniu w ostatnim dniu lub po terminie
        }

        return rezerwacja.obliczCene() * wspolczynnik;
    }

    /**
     * Anuluje rezerwację z możliwością określenia przyczyny anulowania.
     * @param idRezerwacji ID rezerwacji do anulowania
     * @param przyczyna przyczyna anulowania
     * @return true jeśli anulowano pomyślnie
     */
    public boolean anulujRezerwacje(int idRezerwacji, String przyczyna) {
        Optional<Rezerwacja> rezerwacjaOpt = rezerwacjeDAO.pobierz(idRezerwacji);
        if (rezerwacjaOpt.isEmpty()) {
            return false;
        }

        Rezerwacja r = rezerwacjaOpt.get();
        if (r.getStatus() == Rezerwacja.Status.WYMELDOWANA ||
            r.getStatus() == Rezerwacja.Status.ANULOWANA) {
            return false;
        }

        // Ustaw przyczynę anulowania w obiekcie rezerwacji
        r.anuluj(przyczyna);

        // Ustaw dostępność pokoju po anulowaniu rezerwacji
        pokojeDAO.ustawDostepnosc(r.getPokoj().getNumer(), true);

        return rezerwacjeDAO.aktualizuj(r);
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
