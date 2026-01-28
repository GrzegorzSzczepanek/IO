package hotel.model;

import java.util.Objects;

/**
 * Klasa reprezentująca gościa hotelowego.
 * Przechowuje dane osobowe gościa.
 */
public class Gosc {
    
    private int id;
    private String imie;
    private String nazwisko;
    private String email;
    
    private static int nextId = 1;
    
    /**
     * Konstruktor tworzący nowego gościa.
     * @param imie imię gościa
     * @param nazwisko nazwisko gościa
     * @param email adres email gościa
     */
    public Gosc(String imie, String nazwisko, String email) {
        if (imie == null || imie.trim().isEmpty()) {
            throw new IllegalArgumentException("Imię nie może być puste");
        }
        if (nazwisko == null || nazwisko.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwisko nie może być puste");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Nieprawidłowy adres email");
        }
        this.id = nextId++;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.email = email;
    }
    
    /**
     * Konstruktor z podanym ID (do odtwarzania z bazy danych).
     * @param id identyfikator gościa
     * @param imie imię gościa
     * @param nazwisko nazwisko gościa
     * @param email adres email gościa
     */
    public Gosc(int id, String imie, String nazwisko, String email) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID musi być większe od 0");
        }
        if (imie == null || imie.trim().isEmpty()) {
            throw new IllegalArgumentException("Imię nie może być puste");
        }
        if (nazwisko == null || nazwisko.trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwisko nie może być puste");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Nieprawidłowy adres email");
        }
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.email = email;
    }
    
    /**
     * Zwraca ID gościa.
     * @return identyfikator gościa
     */
    public int getId() {
        return id;
    }
    
    /**
     * Zwraca imię gościa.
     * @return imię gościa
     */
    public String getImie() {
        return imie;
    }
    
    /**
     * Zwraca nazwisko gościa.
     * @return nazwisko gościa
     */
    public String getNazwisko() {
        return nazwisko;
    }
    
    /**
     * Zwraca pełne imię i nazwisko gościa.
     * @return imię i nazwisko
     */
    public String getPelneNazwisko() {
        return imie + " " + nazwisko;
    }
    
    /**
     * Zwraca email gościa.
     * @return adres email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Ustawia nowy adres email.
     * @param email nowy adres email
     */
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Nieprawidłowy adres email");
        }
        this.email = email;
    }
    
    /**
     * Resetuje licznik ID (używane w testach).
     */
    public static void resetIdCounter() {
        nextId = 1;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gosc gosc = (Gosc) o;
        return id == gosc.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Gosc{" +
                "id=" + id +
                ", imie='" + imie + '\'' +
                ", nazwisko='" + nazwisko + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
