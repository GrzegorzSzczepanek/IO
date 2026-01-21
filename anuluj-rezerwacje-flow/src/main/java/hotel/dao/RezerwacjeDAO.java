package hotel.dao;

import hotel.model.Rezerwacja;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementacja DAO dla rezerwacji.
 * Warstwa: DAO
 * 
 * Przechowuje rezerwacje w pamięci (HashMap).
 * W produkcji zastąpione przez połączenie z bazą danych.
 */
public class RezerwacjeDAO implements IDAO<Rezerwacja> {
    
    private final Map<Integer, Rezerwacja> rezerwacje;
    private int nastepneId;
    
    public RezerwacjeDAO() {
        this.rezerwacje = new HashMap<>();
        this.nastepneId = 1;
    }
    
    // ========== IMPLEMENTACJA IDAO ==========
    
    @Override
    public Rezerwacja pobierz(int id) {
        return rezerwacje.get(id);
    }
    
    @Override
    public void zapisz(Rezerwacja rezerwacja) {
        if (rezerwacja == null) {
            return;
        }
        
        // Automatyczne nadawanie ID jeśli brak
        if (rezerwacja.getId() == 0) {
            rezerwacja.setId(nastepneId++);
        } else {
            // Aktualizuj licznik jeśli podano wyższe ID
            if (rezerwacja.getId() >= nastepneId) {
                nastepneId = rezerwacja.getId() + 1;
            }
        }
        
        rezerwacje.put(rezerwacja.getId(), rezerwacja);
    }
    
    @Override
    public boolean usun(int id) {
        return rezerwacje.remove(id) != null;
    }
    
    @Override
    public List<Rezerwacja> pobierzWszystkie() {
        return new ArrayList<>(rezerwacje.values());
    }
    
    @Override
    public void aktualizuj(Rezerwacja rezerwacja) {
        if (rezerwacja != null && rezerwacje.containsKey(rezerwacja.getId())) {
            rezerwacje.put(rezerwacja.getId(), rezerwacja);
        }
    }
    
    // ========== METODY SPECYFICZNE DLA REZERWACJI ==========
    
    /**
     * Pobiera rezerwacje dla danego gościa.
     * @param goscId ID gościa
     * @return lista rezerwacji gościa
     */
    public List<Rezerwacja> pobierzDlaGoscia(int goscId) {
        return rezerwacje.values().stream()
                .filter(r -> r.getGosc() != null && r.getGosc().getId() == goscId)
                .collect(Collectors.toList());
    }
    
    /**
     * Pobiera rezerwacje dla danego pokoju.
     * @param pokojNumer numer pokoju
     * @return lista rezerwacji pokoju
     */
    public List<Rezerwacja> pobierzDlaPokoju(int pokojNumer) {
        return rezerwacje.values().stream()
                .filter(r -> r.getPokoj() != null && r.getPokoj().getNumer() == pokojNumer)
                .collect(Collectors.toList());
    }
    
    /**
     * Pobiera rezerwacje w danym okresie.
     * @param dataOd data początkowa
     * @param dataDo data końcowa
     * @return lista rezerwacji w okresie
     */
    public List<Rezerwacja> pobierzWOkresie(LocalDate dataOd, LocalDate dataDo) {
        return rezerwacje.values().stream()
                .filter(r -> !r.getDataDo().isBefore(dataOd) && !r.getDataOd().isAfter(dataDo))
                .collect(Collectors.toList());
    }
    
    /**
     * Pobiera rezerwacje o danym statusie.
     * @param status status rezerwacji
     * @return lista rezerwacji o danym statusie
     */
    public List<Rezerwacja> pobierzPoStatusie(Rezerwacja.StatusRezerwacji status) {
        return rezerwacje.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Sprawdza czy pokój jest dostępny w danym terminie.
     * @param pokojNumer numer pokoju
     * @param dataOd data początkowa
     * @param dataDo data końcowa
     * @return true jeśli pokój jest dostępny
     */
    public boolean czyPokojDostepny(int pokojNumer, LocalDate dataOd, LocalDate dataDo) {
        return rezerwacje.values().stream()
                .filter(r -> r.getPokoj() != null && r.getPokoj().getNumer() == pokojNumer)
                .filter(r -> r.getStatus() != Rezerwacja.StatusRezerwacji.ANULOWANA)
                .noneMatch(r -> !r.getDataDo().isBefore(dataOd) && !r.getDataOd().isAfter(dataDo));
    }
    
    /**
     * Zwraca liczbę rezerwacji.
     * @return liczba rezerwacji
     */
    public int liczbaRezerwacji() {
        return rezerwacje.size();
    }
    
    /**
     * Czyści wszystkie rezerwacje i resetuje licznik.
     */
    public void wyczyscWszystko() {
        rezerwacje.clear();
        nastepneId = 1;
    }
}
