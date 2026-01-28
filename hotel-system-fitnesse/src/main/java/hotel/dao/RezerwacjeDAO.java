package hotel.dao;

import hotel.model.Gosc;
import hotel.model.Pokoj;
import hotel.model.Rezerwacja;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementacja DAO dla encji Rezerwacja.
 * Przechowuje dane w pamięci (symulacja bazy danych).
 */
public class RezerwacjeDAO implements IDAO<Rezerwacja, Integer> {
    
    private final Map<Integer, Rezerwacja> storage = new HashMap<>();
    
    @Override
    public Optional<Rezerwacja> pobierz(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public Rezerwacja zapisz(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            throw new IllegalArgumentException("Rezerwacja nie może być null");
        }
        storage.put(rezerwacja.getId(), rezerwacja);
        return rezerwacja;
    }
    
    @Override
    public boolean usun(Integer id) {
        return storage.remove(id) != null;
    }
    
    @Override
    public List<Rezerwacja> pobierzWszystkie() {
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public boolean aktualizuj(Rezerwacja rezerwacja) {
        if (rezerwacja == null || !storage.containsKey(rezerwacja.getId())) {
            return false;
        }
        storage.put(rezerwacja.getId(), rezerwacja);
        return true;
    }
    
    /**
     * Pobiera rezerwacje dla danego gościa.
     * @param gosc gość
     * @return lista rezerwacji gościa
     */
    public List<Rezerwacja> pobierzDlaGoscia(Gosc gosc) {
        return storage.values().stream()
                .filter(r -> r.getGosc().equals(gosc))
                .collect(Collectors.toList());
    }
    
    /**
     * Pobiera rezerwacje dla danego pokoju.
     * @param pokoj pokój
     * @return lista rezerwacji pokoju
     */
    public List<Rezerwacja> pobierzDlaPokoju(Pokoj pokoj) {
        return storage.values().stream()
                .filter(r -> r.getPokoj().equals(pokoj))
                .collect(Collectors.toList());
    }
    
    /**
     * Pobiera rezerwacje o określonym statusie.
     * @param status status rezerwacji
     * @return lista rezerwacji o danym statusie
     */
    public List<Rezerwacja> pobierzPoStatusie(Rezerwacja.Status status) {
        return storage.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Pobiera rezerwacje w danym przedziale dat.
     * @param dataOd data początkowa
     * @param dataDo data końcowa
     * @return lista rezerwacji w przedziale
     */
    public List<Rezerwacja> pobierzWPrzedzialeData(LocalDate dataOd, LocalDate dataDo) {
        return storage.values().stream()
                .filter(r -> !r.getDataDo().isBefore(dataOd) && !r.getDataOd().isAfter(dataDo))
                .collect(Collectors.toList());
    }
    
    /**
     * Sprawdza czy pokój jest dostępny w danym przedziale dat.
     * @param pokoj pokój do sprawdzenia
     * @param dataOd data początkowa
     * @param dataDo data końcowa
     * @return true jeśli pokój jest dostępny
     */
    public boolean czyPokojDostepny(Pokoj pokoj, LocalDate dataOd, LocalDate dataDo) {
        return storage.values().stream()
                .filter(r -> r.getPokoj().equals(pokoj))
                .filter(r -> r.getStatus() != Rezerwacja.Status.ANULOWANA)
                .filter(r -> r.getStatus() != Rezerwacja.Status.WYMELDOWANA)
                .noneMatch(r -> koliduja(r.getDataOd(), r.getDataDo(), dataOd, dataDo));
    }
    
    /**
     * Sprawdza czy dwa przedziały dat kolidują.
     */
    private boolean koliduja(LocalDate od1, LocalDate do1, LocalDate od2, LocalDate do2) {
        return !do1.isBefore(od2) && !od1.isAfter(do2);
    }
    
    /**
     * Pobiera aktywne rezerwacje (nie anulowane i nie wymeldowane).
     * @return lista aktywnych rezerwacji
     */
    public List<Rezerwacja> pobierzAktywne() {
        return storage.values().stream()
                .filter(r -> r.getStatus() != Rezerwacja.Status.ANULOWANA)
                .filter(r -> r.getStatus() != Rezerwacja.Status.WYMELDOWANA)
                .collect(Collectors.toList());
    }
    
    /**
     * Oblicza sumę przychodów z rezerwacji.
     * @return suma przychodów
     */
    public double obliczSumePrzychodow() {
        return storage.values().stream()
                .filter(r -> r.getStatus() != Rezerwacja.Status.ANULOWANA)
                .mapToDouble(Rezerwacja::obliczCene)
                .sum();
    }
    
    /**
     * Czyści całą bazę (używane w testach).
     */
    public void wyczysc() {
        storage.clear();
    }
    
    /**
     * Zwraca liczbę zapisanych rezerwacji.
     * @return liczba rezerwacji
     */
    public int liczba() {
        return storage.size();
    }
}
