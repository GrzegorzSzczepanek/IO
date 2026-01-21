package hotel.dao;

import java.util.List;
import java.util.Optional;

/**
 * Generyczny interfejs DAO (Data Access Object).
 * Definiuje podstawowe operacje CRUD dla encji.
 * @param <T> typ encji
 * @param <ID> typ identyfikatora encji
 */
public interface IDAO<T, ID> {
    
    /**
     * Pobiera encję po identyfikatorze.
     * @param id identyfikator encji
     * @return Optional z encją lub pusty
     */
    Optional<T> pobierz(ID id);
    
    /**
     * Zapisuje nową encję.
     * @param entity encja do zapisania
     * @return zapisana encja
     */
    T zapisz(T entity);
    
    /**
     * Usuwa encję po identyfikatorze.
     * @param id identyfikator encji do usunięcia
     * @return true jeśli usunięto pomyślnie
     */
    boolean usun(ID id);
    
    /**
     * Pobiera wszystkie encje.
     * @return lista wszystkich encji
     */
    List<T> pobierzWszystkie();
    
    /**
     * Aktualizuje istniejącą encję.
     * @param entity encja do aktualizacji
     * @return true jeśli zaktualizowano pomyślnie
     */
    boolean aktualizuj(T entity);
}
