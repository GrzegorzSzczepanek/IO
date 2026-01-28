package hotel.dao;

import hotel.model.Pokoj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementacja DAO dla encji Pokoj.
 * Przechowuje dane w pamięci (symulacja bazy danych).
 */
public class PokojeDAO implements IDAO<Pokoj, Integer> {
    
    private final Map<Integer, Pokoj> storage = new HashMap<>();
    private final Map<Integer, Boolean> dostepnosc = new HashMap<>();
    
    @Override
    public Optional<Pokoj> pobierz(Integer numer) {
        return Optional.ofNullable(storage.get(numer));
    }
    
    @Override
    public Pokoj zapisz(Pokoj pokoj) {
        if (pokoj == null) {
            throw new IllegalArgumentException("Pokój nie może być null");
        }
        storage.put(pokoj.getNumer(), pokoj);
        dostepnosc.putIfAbsent(pokoj.getNumer(), true);
        return pokoj;
    }
    
    @Override
    public boolean usun(Integer numer) {
        dostepnosc.remove(numer);
        return storage.remove(numer) != null;
    }
    
    @Override
    public List<Pokoj> pobierzWszystkie() {
        return new ArrayList<>(storage.values());
    }
    
    @Override
    public boolean aktualizuj(Pokoj pokoj) {
        if (pokoj == null || !storage.containsKey(pokoj.getNumer())) {
            return false;
        }
        storage.put(pokoj.getNumer(), pokoj);
        return true;
    }
    
    /**
     * Pobiera listę dostępnych pokoi.
     * @return lista dostępnych pokoi
     */
    public List<Pokoj> pobierzDostepne() {
        return storage.entrySet().stream()
                .filter(e -> dostepnosc.getOrDefault(e.getKey(), true))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
    
    /**
     * Pobiera pokoje określonego typu.
     * @param typ typ pokoju
     * @return lista pokoi danego typu
     */
    public List<Pokoj> pobierzPoTypie(String typ) {
        return storage.values().stream()
                .filter(p -> p.getTyp().equalsIgnoreCase(typ))
                .collect(Collectors.toList());
    }
    
    /**
     * Ustawia dostępność pokoju.
     * @param numer numer pokoju
     * @param czyDostepny czy pokój jest dostępny
     * @return true jeśli operacja się powiodła
     */
    public boolean ustawDostepnosc(int numer, boolean czyDostepny) {
        if (!storage.containsKey(numer)) {
            return false;
        }
        dostepnosc.put(numer, czyDostepny);
        return true;
    }
    
    /**
     * Sprawdza czy pokój jest dostępny.
     * @param numer numer pokoju
     * @return true jeśli pokój jest dostępny
     */
    public boolean czyDostepny(int numer) {
        return dostepnosc.getOrDefault(numer, false);
    }
    
    /**
     * Pobiera pokoje w przedziale cenowym.
     * @param cenaMin minimalna cena
     * @param cenaMax maksymalna cena
     * @return lista pokoi w przedziale cenowym
     */
    public List<Pokoj> pobierzWPrzedzialeCanowym(double cenaMin, double cenaMax) {
        return storage.values().stream()
                .filter(p -> p.getCenaBazowa() >= cenaMin && p.getCenaBazowa() <= cenaMax)
                .collect(Collectors.toList());
    }
    
    /**
     * Czyści całą bazę (używane w testach).
     */
    public void wyczysc() {
        storage.clear();
        dostepnosc.clear();
    }
    
    /**
     * Zwraca liczbę zapisanych pokoi.
     * @return liczba pokoi
     */
    public int liczba() {
        return storage.size();
    }
}
