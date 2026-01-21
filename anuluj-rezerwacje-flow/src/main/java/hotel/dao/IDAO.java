package hotel.dao;

import java.util.List;

/**
 * Generyczny interfejs DAO (Data Access Object).
 * Warstwa: DAO
 * 
 * @param <T> typ encji
 */
public interface IDAO<T> {
    
    /**
     * Pobiera encję po ID.
     * @param id identyfikator
     * @return encja lub null jeśli nie znaleziono
     */
    T pobierz(int id);
    
    /**
     * Zapisuje nową encję.
     * @param entity encja do zapisania
     */
    void zapisz(T entity);
    
    /**
     * Usuwa encję po ID.
     * @param id identyfikator
     * @return true jeśli usunięto
     */
    boolean usun(int id);
    
    /**
     * Pobiera wszystkie encje.
     * @return lista wszystkich encji
     */
    List<T> pobierzWszystkie();
    
    /**
     * Aktualizuje istniejącą encję.
     * @param entity encja do aktualizacji
     */
    void aktualizuj(T entity);
}
