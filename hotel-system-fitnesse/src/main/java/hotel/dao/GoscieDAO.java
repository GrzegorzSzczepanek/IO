package hotel.dao;

import hotel.model.Gosc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementacja DAO dla encji Gosc.
 * Przechowuje dane w pamięci (symulacja bazy danych).
 */
public class GoscieDAO implements IDAO<Gosc, Integer> {
    
    private final Map<Integer, Gosc> storage = new HashMap<>();
    
    @Override
    public Optional<Gosc> pobierz(Integer id) {
        return Optional.ofNullable(storage.get(id));
    }
    
    @Override
    public Gosc zapisz(Gosc gosc) {
        if (gosc == null) {
            throw new IllegalArgumentException("Gość nie może być null");
        }
        storage.put(gosc.getId(), gosc);
        return gosc;
    }
    
    @Override
    public boolean usun(Integer id) {
        return storage.remove(id) != null;
    }
    
    @Override
    public List<Gosc> pobierzWszystkie() {
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public boolean aktualizuj(Gosc gosc) {
        if (gosc == null || !storage.containsKey(gosc.getId())) {
            return false;
        }
        storage.put(gosc.getId(), gosc);
        return true;
    }
    
    /**
     * Wyszukuje gościa po adresie email.
     * @param email adres email
     * @return Optional z gościem lub pusty
     */
    public Optional<Gosc> znajdzPoEmail(String email) {
        return storage.values().stream()
                .filter(g -> g.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
    
    /**
     * Wyszukuje gości po nazwisku.
     * @param nazwisko nazwisko do wyszukania
     * @return lista gości o podanym nazwisku
     */
    public List<Gosc> znajdzPoNazwisku(String nazwisko) {
        return storage.values().stream()
                .filter(g -> g.getNazwisko().equalsIgnoreCase(nazwisko))
                .toList();
    }
    
    /**
     * Sprawdza czy istnieje gość o podanym emailu.
     * @param email adres email
     * @return true jeśli istnieje
     */
    public boolean istniejeEmail(String email) {
        return storage.values().stream()
                .anyMatch(g -> g.getEmail().equalsIgnoreCase(email));
    }
    
    /**
     * Czyści całą bazę (używane w testach).
     */
    public void wyczysc() {
        storage.clear();
    }
    
    /**
     * Zwraca liczbę zapisanych gości.
     * @return liczba gości
     */
    public int liczba() {
        return storage.size();
    }
}
