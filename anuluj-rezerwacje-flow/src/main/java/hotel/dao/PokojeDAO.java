package hotel.dao;

import hotel.model.Pokoj;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO dla pokoi.
 */
public class PokojeDAO implements IDAO<Pokoj> {
    private final Map<Integer, Pokoj> pokoje = new HashMap<>();
    
    @Override
    public Pokoj pobierz(int numer) { return pokoje.get(numer); }
    
    @Override
    public void zapisz(Pokoj pokoj) {
        if (pokoj != null) pokoje.put(pokoj.getNumer(), pokoj);
    }
    
    @Override
    public boolean usun(int numer) { return pokoje.remove(numer) != null; }
    
    @Override
    public List<Pokoj> pobierzWszystkie() { return new ArrayList<>(pokoje.values()); }
    
    @Override
    public void aktualizuj(Pokoj pokoj) {
        if (pokoj != null && pokoje.containsKey(pokoj.getNumer())) {
            pokoje.put(pokoj.getNumer(), pokoj);
        }
    }
    
    public List<Pokoj> pobierzDostepne() {
        return pokoje.values().stream()
                .filter(Pokoj::czyDostepny)
                .collect(Collectors.toList());
    }
    
    public boolean czyIstnieje(int numer) { return pokoje.containsKey(numer); }
    
    public void wyczyscWszystko() { pokoje.clear(); }
}
