package hotel.dao;

import hotel.model.Gosc;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO dla gości.
 */
public class GoscieDAO implements IDAO<Gosc> {
    private final Map<Integer, Gosc> goscie = new HashMap<>();
    private int nastepneId = 1;
    
    @Override
    public Gosc pobierz(int id) { return goscie.get(id); }
    
    @Override
    public void zapisz(Gosc gosc) {
        if (gosc == null) return;
        goscie.put(gosc.getId(), gosc);
        if (gosc.getId() >= nastepneId) nastepneId = gosc.getId() + 1;
    }
    
    @Override
    public boolean usun(int id) { return goscie.remove(id) != null; }
    
    @Override
    public List<Gosc> pobierzWszystkie() { return new ArrayList<>(goscie.values()); }
    
    @Override
    public void aktualizuj(Gosc gosc) {
        if (gosc != null && goscie.containsKey(gosc.getId())) {
            goscie.put(gosc.getId(), gosc);
        }
    }
    
    public Gosc znajdzPoEmail(String email) {
        return goscie.values().stream()
                .filter(g -> g.getEmail().equalsIgnoreCase(email))
                .findFirst().orElse(null);
    }
    
    public List<Gosc> znajdzPoNazwisku(String nazwisko) {
        return goscie.values().stream()
                .filter(g -> g.getNazwisko().equalsIgnoreCase(nazwisko))
                .collect(Collectors.toList());
    }
    
    public int liczbaGosci() { return goscie.size(); }
    
    public void wyczyscWszystko() {
        goscie.clear();
        nastepneId = 1;
    }
}

